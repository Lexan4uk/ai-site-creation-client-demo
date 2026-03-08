package com.example.ai_image_client.ui.generation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.repository.GenerationResult
import com.example.ai_image_client.repository.ImageTypeData
import com.example.ai_image_client.repository.RequestStatus
import com.example.ai_image_client.repository.StyleData

@Composable
fun ResultsView(
    results: List<GenerationResult>,
    types: List<ImageTypeData>,
    styles: List<StyleData>,
    successCount: Int,
    failedCount: Int,
    onOpenGallery: () -> Unit,
    onNewGeneration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typesMap = types.associateBy { it.id }
    val stylesMap = styles.associateBy { it.id }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Summary
        Text(
            text = buildString {
                append("Готово! $successCount из ${results.size} успешно")
                if (failedCount > 0) append(", $failedCount ошибок")
            },
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(12.dp))

        // Results list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(results) { result ->
                val typeName = typesMap[result.imageTypeId]?.name ?: "Тип #${result.imageTypeId}"
                val styleName = stylesMap[result.styleId]?.name ?: "Стиль #${result.styleId}"
                val icon = when (result.status) {
                    RequestStatus.DONE -> "\u2705"
                    RequestStatus.FAILED -> "\u274C"
                    RequestStatus.RUNNING -> "\u23F3"
                }
                val detail = when (result.status) {
                    RequestStatus.DONE -> "создан ассет #${result.createdAssetId}"
                    RequestStatus.FAILED -> "ОШИБКА: ${result.errorMessage ?: "неизвестная"}"
                    RequestStatus.RUNNING -> "выполняется..."
                }

                Text(
                    text = "$icon $typeName \u00D7 $styleName \u2014 $detail",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Action buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onOpenGallery, modifier = Modifier.weight(1f)) {
                Text("Открыть в галерее")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onNewGeneration, modifier = Modifier.weight(1f)) {
                Text("Новая генерация")
            }
        }
    }
}
