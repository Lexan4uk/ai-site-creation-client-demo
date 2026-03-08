package com.example.ai_image_client.repository

import java.time.OffsetDateTime

data class AssetData(
    val id: Long,
    val imageTypeId: Long,
    val styleId: Long,
    val fileUri: String,
    val createdAt: OffsetDateTime,
)

interface AssetRepository {
    /**
     * GET /assets?imageTypeId=T[&styleId=S]&page=P&size=N
     * Возвращает страницу ассетов. Пустой список — конец данных.
     */
    suspend fun getByFilter(
        imageTypeId: Long,
        styleId: Long? = null,
        page: Int = 0,
        size: Int = 20,
    ): List<AssetData>
}
