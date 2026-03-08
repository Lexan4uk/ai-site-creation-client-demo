package com.example.ai_image_client.ui.generation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.ai_image_client.navigation.GalleryTab
import com.example.ai_image_client.viewmodel.GenerationPhase
import com.example.ai_image_client.viewmodel.GenerationScreenModel

@Composable
fun GenerationContent(screenModel: GenerationScreenModel) {
    val state by screenModel.state.collectAsState()
    val tabNavigator = LocalTabNavigator.current

    var showCreateTypeDialog by remember { mutableStateOf(false) }
    var showCreateStyleDialog by remember { mutableStateOf(false) }

    when (state.phase) {
        GenerationPhase.CHECKING,
        GenerationPhase.LOADING -> {
            GeneratingView(totalImages = state.totalImages)
        }

        GenerationPhase.DEDUP_DIALOG -> {
            // Показываем форму под диалогом (визуально форма заблокирована)
            GenerationFormContent(
                state = state,
                screenModel = screenModel,
                onShowCreateTypeDialog = { showCreateTypeDialog = true },
                onShowCreateStyleDialog = { showCreateStyleDialog = true },
            )

            // Диалог дедупликации
            val check = state.checkResult
            if (check != null) {
                AlertDialog(
                    onDismissRequest = { screenModel.cancelDedup() },
                    title = { Text("Обнаружены дубликаты") },
                    text = {
                        Text(
                            "Найдено ${check.duplicateCount} из ${check.totalCount} дубликатов.\n" +
                                    "Новых: ${check.newCount}.\n\n" +
                                    "Что сделать с дубликатами?"
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { screenModel.overwriteAll() }) {
                            Text("Пересоздать все")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { screenModel.skipDuplicates() }) {
                            Text("Пропустить")
                        }
                    },
                )
            }
        }

        GenerationPhase.RESULTS -> {
            ResultsView(
                results = state.results,
                types = state.types,
                styles = state.styles,
                successCount = state.successCount,
                failedCount = state.failedCount,
                onOpenGallery = {
                    screenModel.resetForm()
                    tabNavigator.current = GalleryTab
                },
                onNewGeneration = { screenModel.resetForm() },
            )
        }

        GenerationPhase.FORM -> {
            GenerationFormContent(
                state = state,
                screenModel = screenModel,
                onShowCreateTypeDialog = { showCreateTypeDialog = true },
                onShowCreateStyleDialog = { showCreateStyleDialog = true },
            )
        }
    }

    // Dialogs
    if (showCreateTypeDialog) {
        CreateItemDialog(
            title = "Новый тип",
            onConfirm = { name, prompt -> screenModel.createType(name, prompt) },
            onDismiss = { showCreateTypeDialog = false },
        )
    }

    if (showCreateStyleDialog) {
        CreateItemDialog(
            title = "Новый стиль",
            onConfirm = { name, prompt -> screenModel.createStyle(name, prompt) },
            onDismiss = { showCreateStyleDialog = false },
        )
    }

    // Error dialog
    val error = state.error
    if (error != null) {
        AlertDialog(
            onDismissRequest = { screenModel.dismissError() },
            title = { Text("Ошибка") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { screenModel.dismissError() }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun GenerationFormContent(
    state: com.example.ai_image_client.viewmodel.GenerationState,
    screenModel: GenerationScreenModel,
    onShowCreateTypeDialog: () -> Unit,
    onShowCreateStyleDialog: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top: type & style selectors
        Row(modifier = Modifier.weight(1f)) {
            CheckboxList(
                title = "Типы",
                items = state.types.map {
                    CheckboxItem(it.id, it.name, it.createdByUserId)
                },
                favoriteIds = state.favoriteTypeIds,
                checkedIds = state.checkedTypeIds,
                searchQuery = state.typeSearchQuery,
                onSearchChange = { screenModel.searchTypes(it) },
                onToggleCheck = { screenModel.toggleType(it) },
                onToggleFavorite = { screenModel.toggleFavoriteType(it) },
                onDelete = { screenModel.removeType(it) },
                onCreateClick = onShowCreateTypeDialog,
                modifier = Modifier.weight(1f),
            )

            VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            CheckboxList(
                title = "Стили",
                items = state.styles.map {
                    CheckboxItem(it.id, it.name, it.createdByUserId)
                },
                favoriteIds = state.favoriteStyleIds,
                checkedIds = state.checkedStyleIds,
                searchQuery = state.styleSearchQuery,
                onSearchChange = { screenModel.searchStyles(it) },
                onToggleCheck = { screenModel.toggleStyle(it) },
                onToggleFavorite = { screenModel.toggleFavoriteStyle(it) },
                onDelete = { screenModel.removeStyle(it) },
                onCreateClick = onShowCreateStyleDialog,
                modifier = Modifier.weight(1f),
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Bottom: prompt panel
        PromptPanel(
            prompt = state.prompt,
            selectedModelConfig = state.selectedModelConfig,
            selectedAspectRatio = state.selectedAspectRatio,
            totalImages = state.totalImages,
            canGenerate = state.canGenerate,
            onPromptChange = { screenModel.setPrompt(it) },
            onModelChange = { screenModel.selectModel(it) },
            onAspectRatioChange = { screenModel.selectAspectRatio(it) },
            onGenerate = { screenModel.generate() },
        )
    }
}
