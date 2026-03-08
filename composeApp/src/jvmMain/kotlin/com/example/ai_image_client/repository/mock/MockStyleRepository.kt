package com.example.ai_image_client.repository.mock

import com.example.ai_image_client.repository.StyleData
import com.example.ai_image_client.repository.StyleListResponse
import com.example.ai_image_client.repository.StyleRepository

class MockStyleRepository : StyleRepository {

    private var nextId = 7L

    private val styles = mutableListOf(
        StyleData(1, null, "Неопределённый", ""),
        StyleData(2, 2, "Айвазовский", "in style of Aivazovsky, sea, waves, romantic"),
        StyleData(3, 2, "Warcraft", "World of Warcraft style, fantasy, epic"),
        StyleData(4, 3, "Elden Ring", "Elden Ring dark fantasy, gothic, mysterious"),
        StyleData(5, 2, "Помпейская фреска", "Pompeii fresco style, ancient Roman, mural"),
        StyleData(6, 3, "Минимализм", "minimalist, clean, simple lines, white space"),
    )

    private val favoriteIds = mutableSetOf(2L, 6L)

    override suspend fun getAll(): StyleListResponse =
        StyleListResponse(
            styles = styles.toList(),
            favoriteStyleIds = favoriteIds.toList(),
        )

    override suspend fun addFavorite(id: Long) {
        favoriteIds.add(id)
    }

    override suspend fun removeFavorite(id: Long) {
        favoriteIds.remove(id)
    }

    override suspend fun create(name: String, stylePrompt: String): StyleData {
        val newStyle = StyleData(nextId++, 2, name, stylePrompt)
        styles.add(newStyle)
        return newStyle
    }

    override suspend fun remove(id: Long) {
        styles.removeAll { it.id == id }
        favoriteIds.remove(id)
    }
}
