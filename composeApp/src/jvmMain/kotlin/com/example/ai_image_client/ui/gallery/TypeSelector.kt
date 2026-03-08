package com.example.ai_image_client.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.repository.ImageTypeData
import com.example.ai_image_client.ui.common.FavoriteStar
import com.example.ai_image_client.ui.common.SearchField

@Composable
fun TypeSelector(
    types: List<ImageTypeData>,
    favoriteIds: Set<Long>,
    selectedType: ImageTypeData?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onTypeClick: (ImageTypeData) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filtered = types.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }
    val favorites = filtered.filter { it.id in favoriteIds }
    val others = filtered.filter { it.id !in favoriteIds }

    Column(modifier = modifier.padding(8.dp)) {
        SearchField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Поиск типа...",
        )

        Spacer(Modifier.height(8.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Секция "Избранные"
            if (favorites.isNotEmpty()) {
                SectionHeader("Избранные")
                favorites.forEach { type ->
                    TypeItem(
                        type = type,
                        isFavorite = true,
                        isSelected = selectedType?.id == type.id,
                        onClick = { onTypeClick(type) },
                        onToggleFavorite = { onToggleFavorite(type.id) },
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Секция "Все"
            SectionHeader("Все")
            others.forEach { type ->
                TypeItem(
                    type = type,
                    isFavorite = type.id in favoriteIds,
                    isSelected = selectedType?.id == type.id,
                    onClick = { onTypeClick(type) },
                    onToggleFavorite = { onToggleFavorite(type.id) },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun TypeItem(
    type: ImageTypeData,
    isFavorite: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val isSystem = type.id == 1L
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSystem -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = type.name,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.weight(1f),
        )
        FavoriteStar(
            isFavorite = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp),
        )
    }
}
