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
import com.example.ai_image_client.repository.AssetData
import com.example.ai_image_client.repository.AssetRepository
import com.example.ai_image_client.repository.ImageTypeData
import com.example.ai_image_client.repository.ImageTypeRepository
import com.example.ai_image_client.repository.StyleData
import com.example.ai_image_client.repository.StyleRepository

private const val PAGE_SIZE = 20

data class GalleryState(
    val isLoading: Boolean = true,
    val types: List<ImageTypeData> = emptyList(),
    val favoriteTypeIds: Set<Long> = emptySet(),
    val selectedType: ImageTypeData? = null,
    val typeSearchQuery: String = "",
    val styles: List<StyleData> = emptyList(),
    val favoriteStyleIds: Set<Long> = emptySet(),
    val selectedStyleId: Long? = null, // null = "Все стили"
    val styleSearchQuery: String = "",
    val stylesMap: Map<Long, StyleData> = emptyMap(),
    val assets: List<AssetData> = emptyList(),
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val viewerAsset: AssetData? = null, // для lightbox
)

class GalleryScreenModel(
    private val imageTypeRepository: ImageTypeRepository,
    private val styleRepository: StyleRepository,
    private val assetRepository: AssetRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state.asStateFlow()

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
                        stylesMap = stylesResponse.styles.associateBy { s -> s.id },
                    )
                }
            } catch (e: Throwable) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки") }
            }
        }
    }

    fun selectType(type: ImageTypeData) {
        _state.update {
            it.copy(
                selectedType = type,
                selectedStyleId = null,
                assets = emptyList(),
                currentPage = 0,
                hasMorePages = true,
            )
        }
        loadAssetsPage(type.id, null, page = 0, reset = true)
    }

    fun selectStyle(styleId: Long?) {
        _state.update {
            it.copy(
                selectedStyleId = styleId,
                assets = emptyList(),
                currentPage = 0,
                hasMorePages = true,
            )
        }
        val typeId = _state.value.selectedType?.id ?: return
        loadAssetsPage(typeId, styleId, page = 0, reset = true)
    }

    /** Подгрузка следующей страницы (infinite scroll). */
    fun loadMoreAssets() {
        val s = _state.value
        if (s.isLoadingMore || !s.hasMorePages) return
        val typeId = s.selectedType?.id ?: return
        val nextPage = s.currentPage + 1
        loadAssetsPage(typeId, s.selectedStyleId, nextPage, reset = false)
    }

    private fun loadAssetsPage(typeId: Long, styleId: Long?, page: Int, reset: Boolean) {
        screenModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                val newAssets = withContext(Dispatchers.IO) {
                    assetRepository.getByFilter(typeId, styleId, page, PAGE_SIZE)
                }
                _state.update {
                    it.copy(
                        assets = if (reset) newAssets else it.assets + newAssets,
                        currentPage = page,
                        hasMorePages = newAssets.size == PAGE_SIZE,
                        isLoadingMore = false,
                    )
                }
            } catch (e: Throwable) {
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Ошибка загрузки ассетов",
                    )
                }
            }
        }
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

    fun searchTypes(query: String) {
        _state.update { it.copy(typeSearchQuery = query) }
    }

    fun searchStyles(query: String) {
        _state.update { it.copy(styleSearchQuery = query) }
    }

    fun openViewer(asset: AssetData) {
        _state.update { it.copy(viewerAsset = asset) }
    }

    fun closeViewer() {
        _state.update { it.copy(viewerAsset = null) }
    }

    /** Скачивание ассета на устройство (mock — имитация). */
    fun downloadAsset(asset: AssetData) {
        // В реальном приложении: скачать файл по asset.fileUri на диск.
        // В демо-клиенте с моками — просто логируем.
        println("Download asset #${asset.id} — fileUri: ${asset.fileUri}")
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
