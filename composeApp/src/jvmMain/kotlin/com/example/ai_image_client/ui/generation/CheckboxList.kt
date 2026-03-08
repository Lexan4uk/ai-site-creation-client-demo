package com.example.ai_image_client.ui.generation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.ui.common.FavoriteStar
import com.example.ai_image_client.ui.common.SearchField

data class CheckboxItem(
    val id: Long,
    val name: String,
    val createdByUserId: Long?,
)

@Composable
fun CheckboxList(
    title: String,
    items: List<CheckboxItem>,
    favoriteIds: Set<Long>,
    checkedIds: Set<Long>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleCheck: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onCreateClick: () -> Unit,
    maxChecked: Int = 10,
    modifier: Modifier = Modifier,
) {
    // Filter out system (id=1) and apply search
    val visible = items.filter { it.id != 1L }.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }
    val favorites = visible.filter { it.id in favoriteIds }
    val others = visible.filter { it.id !in favoriteIds }
    val limitReached = checkedIds.size >= maxChecked

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(4.dp))

        SearchField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Поиск...",
        )

        Spacer(Modifier.height(4.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Favorites section
            if (favorites.isNotEmpty()) {
                SectionLabel("Избранные")
                favorites.forEach { item ->
                    val isChecked = item.id in checkedIds
                    CheckboxRow(
                        item = item,
                        isChecked = isChecked,
                        isFavorite = true,
                        canDelete = item.createdByUserId != null,
                        enabled = isChecked || !limitReached,
                        onToggleCheck = { onToggleCheck(item.id) },
                        onToggleFavorite = { onToggleFavorite(item.id) },
                        onDelete = { onDelete(item.id) },
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            // Остальные
            if (others.isNotEmpty()) {
                SectionLabel("Все")
            }
            others.forEach { item ->
                val isChecked = item.id in checkedIds
                CheckboxRow(
                    item = item,
                    isChecked = isChecked,
                    isFavorite = item.id in favoriteIds,
                    canDelete = item.createdByUserId != null,
                    enabled = isChecked || !limitReached,
                    onToggleCheck = { onToggleCheck(item.id) },
                    onToggleFavorite = { onToggleFavorite(item.id) },
                    onDelete = { onDelete(item.id) },
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Counter + limit warning
        Text(
            text = if (limitReached) "Выбрано: ${checkedIds.size}/$maxChecked (максимум)"
                   else "Выбрано: ${checkedIds.size}/$maxChecked",
            style = MaterialTheme.typography.labelMedium,
            color = if (limitReached) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(4.dp))

        // Create button
        OutlinedButton(
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Создать")
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun CheckboxRow(
    item: CheckboxItem,
    isChecked: Boolean,
    isFavorite: Boolean,
    canDelete: Boolean,
    enabled: Boolean = true,
    onToggleCheck: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable { onToggleCheck() } else Modifier)
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = isChecked, onCheckedChange = { onToggleCheck() }, enabled = enabled)
        Spacer(Modifier.width(4.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        FavoriteStar(
            isFavorite = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier.size(28.dp),
        )
        if (canDelete) {
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
