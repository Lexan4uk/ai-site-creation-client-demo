package com.example.ai_image_client.repository

enum class RequestStatus { RUNNING, DONE, FAILED }

data class GenerationResult(
    val requestId: Long,
    val imageTypeId: Long,
    val styleId: Long,
    val status: RequestStatus,
    val createdAssetId: Long?,
    val errorMessage: String?,
)

/**
 * Результат POST /generations/check — подсчёт дубликатов без генерации.
 */
data class CheckResult(
    val totalCount: Int,
    val duplicateCount: Int,
    val newCount: Int,
)

data class GenerateParams(
    val userPrompt: String,
    val generationParams: String,
    val imageTypeIds: List<Long>,
    val styleIds: List<Long>,
    val overwriteDuplicates: Boolean = false,
    val provider: String,
    val model: String,
)

interface GenerationRepository {
    /** POST /generations/check — подсчёт дубликатов. */
    suspend fun check(params: GenerateParams): CheckResult
    /** POST /generations — запуск генерации. */
    suspend fun generate(params: GenerateParams): List<GenerationResult>
}
