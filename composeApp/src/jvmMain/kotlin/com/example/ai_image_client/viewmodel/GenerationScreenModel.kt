package com.example.ai_image_client.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ai_image_client.core.AiModelConfig
import com.example.ai_image_client.core.AiModelRegistry
import com.example.ai_image_client.repository.CheckResult
import com.example.ai_image_client.repository.GenerateParams
import com.example.ai_image_client.repository.GenerationRepository
import com.example.ai_image_client.repository.GenerationResult
import com.example.ai_image_client.repository.ImageTypeData
import com.example.ai_image_client.repository.ImageTypeRepository
import com.example.ai_image_client.repository.RequestStatus
import com.example.ai_image_client.repository.StyleData
import com.example.ai_image_client.repository.StyleRepository

private const val MAX_CHECKED = 10

enum class GenerationPhase { FORM, CHECKING, DEDUP_DIALOG, LOADING, RESULTS }

data class GenerationState(
    val isLoading: Boolean = true,
    val types: List<ImageTypeData> = emptyList(),
    val styles: List<StyleData> = emptyList(),
    val favoriteTypeIds: Set<Long> = emptySet(),
    val favoriteStyleIds: Set<Long> = emptySet(),
    val checkedTypeIds: Set<Long> = emptySet(),
    val checkedStyleIds: Set<Long> = emptySet(),
    val typeSearchQuery: String = "",
    val styleSearchQuery: String = "",
    val prompt: String = "",
    val selectedModelId: String = AiModelRegistry.default.id,
    val selectedAspectRatio: String = AiModelRegistry.default.defaultAspectRatio,
    val phase: GenerationPhase = GenerationPhase.FORM,
    val checkResult: CheckResult? = null,
    val results: List<GenerationResult> = emptyList(),
    val error: String? = null,
) {
    val selectedModelConfig: AiModelConfig
        get() = AiModelRegistry.findById(selectedModelId) ?: AiModelRegistry.default

    val totalImages: Int get() = checkedTypeIds.size * checkedStyleIds.size

    val canGenerate: Boolean
        get() = prompt.isNotBlank() && checkedTypeIds.isNotEmpty() && checkedStyleIds.isNotEmpty()
                && phase == GenerationPhase.FORM

    val successCount: Int get() = results.count { it.status == RequestStatus.DONE }
    val failedCount: Int get() = results.count { it.status == RequestStatus.FAILED }
}

class GenerationScreenModel(
    private val imageTypeRepository: ImageTypeRepository,
    private val styleRepository: StyleRepository,
    private val generationRepository: GenerationRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(GenerationState())
    val state: StateFlow<GenerationState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val typesDeferred = async(Dispatchers.IO) { imageTypeRepository.getAll() }
                val stylesDeferred = async(Dispatchers.IO) { styleRepository.getAll() }

                val typesResponse = typesDeferred.await()
                val stylesResponse = stylesDeferred.await()

                _state.update {
                    it.copy(
                        isLoading = false,
                        types = typesResponse.types,
                        favoriteTypeIds = typesResponse.favoriteTypeIds.toSet(),
                        styles = stylesResponse.styles,
                        favoriteStyleIds = stylesResponse.favoriteStyleIds.toSet(),
                    )
                }
            } catch (e: Throwable) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки") }
            }
        }
    }

    fun toggleType(id: Long) {
        _state.update {
            val new = it.checkedTypeIds.toMutableSet()
            if (id in new) new.remove(id)
            else if (new.size < MAX_CHECKED) new.add(id)
            it.copy(checkedTypeIds = new)
        }
    }

    fun toggleStyle(id: Long) {
        _state.update {
            val new = it.checkedStyleIds.toMutableSet()
            if (id in new) new.remove(id)
            else if (new.size < MAX_CHECKED) new.add(id)
            it.copy(checkedStyleIds = new)
        }
    }

    fun setPrompt(value: String) {
        _state.update { it.copy(prompt = value) }
    }

    fun selectModel(modelId: String) {
        val config = AiModelRegistry.findById(modelId) ?: return
        _state.update {
            it.copy(
                selectedModelId = modelId,
                selectedAspectRatio = if (it.selectedAspectRatio in config.aspectRatios)
                    it.selectedAspectRatio else config.defaultAspectRatio,
            )
        }
    }

    fun selectAspectRatio(ratio: String) {
        _state.update { it.copy(selectedAspectRatio = ratio) }
    }

    fun searchTypes(query: String) {
        _state.update { it.copy(typeSearchQuery = query) }
    }

    fun searchStyles(query: String) {
        _state.update { it.copy(styleSearchQuery = query) }
    }

    fun toggleFavoriteType(id: Long) {
        screenModelScope.launch {
            val isFav = id in _state.value.favoriteTypeIds
            withContext(Dispatchers.IO) {
                if (isFav) imageTypeRepository.removeFavorite(id)
                else imageTypeRepository.addFavorite(id)
            }
            _state.update {
                val newFavs = it.favoriteTypeIds.toMutableSet()
                if (isFav) newFavs.remove(id) else newFavs.add(id)
                it.copy(favoriteTypeIds = newFavs)
            }
        }
    }

    fun toggleFavoriteStyle(id: Long) {
        screenModelScope.launch {
            val isFav = id in _state.value.favoriteStyleIds
            withContext(Dispatchers.IO) {
                if (isFav) styleRepository.removeFavorite(id)
                else styleRepository.addFavorite(id)
            }
            _state.update {
                val newFavs = it.favoriteStyleIds.toMutableSet()
                if (isFav) newFavs.remove(id) else newFavs.add(id)
                it.copy(favoriteStyleIds = newFavs)
            }
        }
    }

    fun createType(name: String, prompt: String) {
        screenModelScope.launch {
            try {
                val newType = withContext(Dispatchers.IO) {
                    imageTypeRepository.create(name, prompt)
                }
                _state.update { it.copy(types = it.types + newType) }
            } catch (e: Throwable) {
                _state.update { it.copy(error = e.message ?: "Ошибка создания типа") }
            }
        }
    }

    fun createStyle(name: String, prompt: String) {
        screenModelScope.launch {
            try {
                val newStyle = withContext(Dispatchers.IO) {
                    styleRepository.create(name, prompt)
                }
                _state.update { it.copy(styles = it.styles + newStyle) }
            } catch (e: Throwable) {
                _state.update { it.copy(error = e.message ?: "Ошибка создания стиля") }
            }
        }
    }

    fun removeType(id: Long) {
        screenModelScope.launch {
            withContext(Dispatchers.IO) { imageTypeRepository.remove(id) }
            _state.update {
                it.copy(
                    types = it.types.filter { t -> t.id != id },
                    checkedTypeIds = it.checkedTypeIds - id,
                )
            }
        }
    }

    fun removeStyle(id: Long) {
        screenModelScope.launch {
            withContext(Dispatchers.IO) { styleRepository.remove(id) }
            _state.update {
                it.copy(
                    styles = it.styles.filter { s -> s.id != id },
                    checkedStyleIds = it.checkedStyleIds - id,
                )
            }
        }
    }

    // ─── Двухшаговая генерация ───────────────────────────────────────

    private fun buildParams(overwriteDuplicates: Boolean = false): GenerateParams {
        val s = _state.value
        val config = s.selectedModelConfig
        return GenerateParams(
            userPrompt = s.prompt,
            generationParams = "{\"aspectRatio\":\"${s.selectedAspectRatio}\"}",
            imageTypeIds = s.checkedTypeIds.toList(),
            styleIds = s.checkedStyleIds.toList(),
            overwriteDuplicates = overwriteDuplicates,
            provider = config.provider,
            model = config.model,
        )
    }

    /**
     * Шаг 1: POST /generations/check.
     * Если дубликаты есть → показать диалог (DEDUP_DIALOG).
     * Если нет → сразу запустить генерацию.
     */
    fun generate() {
        val s = _state.value
        if (!s.canGenerate) return

        _state.update { it.copy(phase = GenerationPhase.CHECKING) }

        screenModelScope.launch {
            try {
                val checkResult = withContext(Dispatchers.IO) {
                    generationRepository.check(buildParams())
                }
                if (checkResult.duplicateCount > 0) {
                    _state.update {
                        it.copy(
                            phase = GenerationPhase.DEDUP_DIALOG,
                            checkResult = checkResult,
                        )
                    }
                } else {
                    // Нет дубликатов — сразу генерация
                    runGeneration(overwriteDuplicates = false)
                }
            } catch (e: Throwable) {
                _state.update {
                    it.copy(
                        phase = GenerationPhase.FORM,
                        error = e.message ?: "Ошибка проверки дубликатов",
                    )
                }
            }
        }
    }

    /** Пользователь выбрал «Пропустить дубликаты» в диалоге. */
    fun skipDuplicates() {
        runGeneration(overwriteDuplicates = false)
    }

    /** Пользователь выбрал «Пересоздать все» в диалоге. */
    fun overwriteAll() {
        runGeneration(overwriteDuplicates = true)
    }

    /** Пользователь закрыл диалог — вернуться к форме. */
    fun cancelDedup() {
        _state.update { it.copy(phase = GenerationPhase.FORM, checkResult = null) }
    }

    private fun runGeneration(overwriteDuplicates: Boolean) {
        val s = _state.value
        _state.update { it.copy(phase = GenerationPhase.LOADING, checkResult = null) }

        screenModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    generationRepository.generate(buildParams(overwriteDuplicates))
                }
                _state.update { it.copy(phase = GenerationPhase.RESULTS, results = results) }
            } catch (e: Throwable) {
                val errorMessage = e.message ?: "неизвестная ошибка"
                val syntheticResults = s.checkedTypeIds.flatMap { typeId ->
                    s.checkedStyleIds.map { styleId ->
                        GenerationResult(
                            requestId = -1,
                            imageTypeId = typeId,
                            styleId = styleId,
                            status = RequestStatus.FAILED,
                            createdAssetId = null,
                            errorMessage = errorMessage,
                        )
                    }
                }
                _state.update {
                    it.copy(phase = GenerationPhase.RESULTS, results = syntheticResults)
                }
            }
        }
    }

    fun resetForm() {
        val defaultModel = AiModelRegistry.default
        _state.update {
            it.copy(
                checkedTypeIds = emptySet(),
                checkedStyleIds = emptySet(),
                prompt = "",
                selectedModelId = defaultModel.id,
                selectedAspectRatio = defaultModel.defaultAspectRatio,
                phase = GenerationPhase.FORM,
                checkResult = null,
                results = emptyList(),
                error = null,
            )
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
