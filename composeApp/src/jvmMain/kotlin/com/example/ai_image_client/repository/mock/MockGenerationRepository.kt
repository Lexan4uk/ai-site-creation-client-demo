package com.example.ai_image_client.repository.mock

import com.example.ai_image_client.repository.CheckResult
import com.example.ai_image_client.repository.GenerateParams
import com.example.ai_image_client.repository.GenerationRepository
import com.example.ai_image_client.repository.GenerationResult
import com.example.ai_image_client.repository.RequestStatus
import kotlinx.coroutines.delay

class MockGenerationRepository : GenerationRepository {

    private var nextRequestId = 1000L
    private var nextAssetId = 200L

    /**
     * Режим имитации ошибок.
     * SUCCESS  — всё успешно
     * PARTIAL  — часть запросов FAILED (каждый 3-й)
     * CRASH    — полный сбой (throw, имитация падения сервера)
     */
    var errorMode: ErrorMode = ErrorMode.PARTIAL

    enum class ErrorMode { SUCCESS, PARTIAL, CRASH }

    override suspend fun check(params: GenerateParams): CheckResult {
        delay(500)
        val total = params.imageTypeIds.size * params.styleIds.size
        // Имитация: ~30 % дубликатов
        val duplicates = (total * 0.3).toInt()
        return CheckResult(
            totalCount = total,
            duplicateCount = duplicates,
            newCount = total - duplicates,
        )
    }

    override suspend fun generate(params: GenerateParams): List<GenerationResult> {
        // Имитация задержки сервера
        delay(2000)

        // Полный сбой — имитация падения сервера / connection refused
        if (errorMode == ErrorMode.CRASH) {
            throw RuntimeException("Connection refused: localhost:8080")
        }

        val results = mutableListOf<GenerationResult>()
        var index = 0

        for (typeId in params.imageTypeIds) {
            for (styleId in params.styleIds) {
                index++

                // При overwriteDuplicates = false дубликаты просто не попадают в ответ.
                // Имитация: последняя комбинация — дубликат (пропускается).
                val isLastCombo = typeId == params.imageTypeIds.last() &&
                        styleId == params.styleIds.last() &&
                        !params.overwriteDuplicates &&
                        params.imageTypeIds.size > 1

                if (isLastCombo) continue // дубликат — не попадает в ответ

                // Имитация: каждый 3-й запрос — ошибка AI (в режиме PARTIAL)
                val isError = errorMode == ErrorMode.PARTIAL && index % 3 == 0

                if (isError) {
                    results.add(
                        GenerationResult(
                            requestId = nextRequestId++,
                            imageTypeId = typeId,
                            styleId = styleId,
                            status = RequestStatus.FAILED,
                            createdAssetId = null,
                            errorMessage = listOf(
                                "429 Too Many Requests: rate limit exceeded",
                                "Content policy violation: unsafe content",
                                "500 Internal Server Error: provider unavailable",
                            ).random(),
                        )
                    )
                } else {
                    results.add(
                        GenerationResult(
                            requestId = nextRequestId++,
                            imageTypeId = typeId,
                            styleId = styleId,
                            status = RequestStatus.DONE,
                            createdAssetId = nextAssetId++,
                            errorMessage = null,
                        )
                    )
                }
            }
        }
        return results
    }
}
