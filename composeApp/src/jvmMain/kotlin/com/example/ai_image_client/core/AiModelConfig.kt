package com.example.ai_image_client.core

/**
 * Конфигурация одной AI-модели для генерации изображений.
 *
 * @param id             уникальный ключ (например, "openai-gpt-image-1.5")
 * @param provider       имя провайдера для бэкенда ("openai")
 * @param model          имя модели для бэкенда
 * @param displayName    отображаемое имя в UI
 * @param aspectRatios   допустимые aspect ratio для данной модели
 * @param defaultAspectRatio  ratio по умолчанию
 */
data class AiModelConfig(
    val id: String,
    val provider: String,
    val model: String,
    val displayName: String,
    val aspectRatios: List<String>,
    val defaultAspectRatio: String,
)

/**
 * Реестр доступных моделей.
 * Для добавления новой модели — просто добавить запись в [models].
 */
object AiModelRegistry {

    val models: List<AiModelConfig> = listOf(
        AiModelConfig(
            id = "openai-gpt-image-1",
            provider = "openai",
            model = "gpt-image-1",
            displayName = "OpenAI GPT Image 1",
            aspectRatios = listOf("1:1", "1:1.5", "1.5:1", "auto"),
            defaultAspectRatio = "1:1",
        ),
        AiModelConfig(
            id = "openai-gpt-image-1-mini",
            provider = "openai",
            model = "gpt-image-1-mini",
            displayName = "OpenAI GPT Image 1 Mini",
            aspectRatios = listOf("1:1", "1:1.5", "1.5:1", "auto"),
            defaultAspectRatio = "1:1",
        ),
        AiModelConfig(
            id = "openai-gpt-image-1.5",
            provider = "openai",
            model = "gpt-image-1.5",
            displayName = "OpenAI GPT Image 1.5",
            aspectRatios = listOf("1:1", "1:1.5", "1.5:1", "auto"),
            defaultAspectRatio = "1:1",
        ),
    )

    val default: AiModelConfig = models.first()

    fun findById(id: String): AiModelConfig? = models.find { it.id == id }
}
