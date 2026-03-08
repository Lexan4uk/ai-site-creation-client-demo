package com.example.ai_image_client.repository

data class StyleData(
    val id: Long,
    val createdByUserId: Long?,
    val name: String,
    val stylePrompt: String,
)

/**
 * Ответ GET /styles — стили + массив id избранных.
 */
data class StyleListResponse(
    val styles: List<StyleData>,
    val favoriteStyleIds: List<Long>,
)

interface StyleRepository {
    /** GET /styles — возвращает стили и избранное в одном ответе. */
    suspend fun getAll(): StyleListResponse
    /** POST /styles/{id}/favorite/add */
    suspend fun addFavorite(id: Long)
    /** POST /styles/{id}/favorite/remove */
    suspend fun removeFavorite(id: Long)
    /** POST /styles */
    suspend fun create(name: String, stylePrompt: String): StyleData
    /** POST /styles/{id}/remove */
    suspend fun remove(id: Long)
}
