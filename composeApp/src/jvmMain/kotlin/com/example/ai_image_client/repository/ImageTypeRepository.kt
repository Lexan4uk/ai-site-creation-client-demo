package com.example.ai_image_client.repository

data class ImageTypeData(
    val id: Long,
    val createdByUserId: Long?,
    val name: String,
    val typePrompt: String,
)

/**
 * Ответ GET /image-types — типы + массив id избранных.
 */
data class ImageTypeListResponse(
    val types: List<ImageTypeData>,
    val favoriteTypeIds: List<Long>,
)

interface ImageTypeRepository {
    /** GET /image-types — возвращает типы и избранное в одном ответе. */
    suspend fun getAll(): ImageTypeListResponse
    /** POST /image-types/{id}/favorite/add */
    suspend fun addFavorite(id: Long)
    /** POST /image-types/{id}/favorite/remove */
    suspend fun removeFavorite(id: Long)
    /** POST /image-types */
    suspend fun create(name: String, typePrompt: String): ImageTypeData
    /** POST /image-types/{id}/remove */
    suspend fun remove(id: Long)
}
