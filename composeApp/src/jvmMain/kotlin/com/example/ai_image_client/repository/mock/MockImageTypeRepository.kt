package com.example.ai_image_client.repository.mock

import com.example.ai_image_client.repository.ImageTypeData
import com.example.ai_image_client.repository.ImageTypeListResponse
import com.example.ai_image_client.repository.ImageTypeRepository

class MockImageTypeRepository : ImageTypeRepository {

    private var nextId = 6L

    private val types = mutableListOf(
        ImageTypeData(1, null, "Неопределённый", ""),
        ImageTypeData(2, 2, "Фотография", "realistic photo, high quality, 4k"),
        ImageTypeData(3, 2, "Иконка", "flat icon, simple, vector style"),
        ImageTypeData(4, 2, "Картина", "oil painting, canvas texture, artistic"),
        ImageTypeData(5, 3, "Абстракция", "abstract art, geometric shapes, vibrant colors"),
    )

    private val favoriteIds = mutableSetOf(2L, 3L)

    override suspend fun getAll(): ImageTypeListResponse =
        ImageTypeListResponse(
            types = types.toList(),
            favoriteTypeIds = favoriteIds.toList(),
        )

    override suspend fun addFavorite(id: Long) {
        favoriteIds.add(id)
    }

    override suspend fun removeFavorite(id: Long) {
        favoriteIds.remove(id)
    }

    override suspend fun create(name: String, typePrompt: String): ImageTypeData {
        val newType = ImageTypeData(nextId++, 2, name, typePrompt)
        types.add(newType)
        return newType
    }

    override suspend fun remove(id: Long) {
        types.removeAll { it.id == id }
        favoriteIds.remove(id)
    }
}
