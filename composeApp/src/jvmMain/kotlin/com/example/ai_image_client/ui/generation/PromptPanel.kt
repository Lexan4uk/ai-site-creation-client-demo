package com.example.ai_image_client.ui.generation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.core.AiModelConfig
import com.example.ai_image_client.core.AiModelRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptPanel(
    prompt: String,
    selectedModelConfig: AiModelConfig,
    selectedAspectRatio: String,
    totalImages: Int,
    canGenerate: Boolean,
    onPromptChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onAspectRatioChange: (String) -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(12.dp)) {
        // Промпт
        Text("Промпт:", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = { if (it.length <= 2000) onPromptChange(it) },
            placeholder = { Text("Опишите изображение...") },
            supportingText = { Text("${prompt.length}/2000") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        // Модель + Aspect Ratio в одну строку
        Row(verticalAlignment = Alignment.Top) {
            // Dropdown модели
            ModelDropdown(
                selectedConfig = selectedModelConfig,
                onModelChange = onModelChange,
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(12.dp))

            // Dropdown aspect ratio
            AspectRatioDropdown(
                aspectRatios = selectedModelConfig.aspectRatios,
                selected = selectedAspectRatio,
                onSelect = onAspectRatioChange,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Счётчик (место зарезервировано всегда, чтобы не прыгал layout)
        Text(
            text = if (totalImages > 0) "Будет сгенерировано: $totalImages изображений" else " ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))

        // Кнопка генерации
        Button(
            onClick = onGenerate,
            enabled = canGenerate,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Сгенерировать")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelDropdown(
    selectedConfig: AiModelConfig,
    onModelChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text("Модель:", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selectedConfig.displayName,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                AiModelRegistry.models.forEach { config ->
                    DropdownMenuItem(
                        text = { Text(config.displayName) },
                        onClick = {
                            onModelChange(config.id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AspectRatioDropdown(
    aspectRatios: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text("Aspect Ratio:", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                aspectRatios.forEach { ratio ->
                    DropdownMenuItem(
                        text = { Text(ratio) },
                        onClick = {
                            onSelect(ratio)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
