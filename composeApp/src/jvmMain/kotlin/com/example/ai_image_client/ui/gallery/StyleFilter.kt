package com.example.ai_image_client.ui.gallery

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.repository.StyleData
import com.example.ai_image_client.ui.common.FavoriteStar
import com.example.ai_image_client.ui.common.SearchField

@Composable
fun StyleFilter(
    styles: List<StyleData>,
    favoriteIds: Set<Long>,
    selectedStyleId: Long?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onStyleSelect: (Long?) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Фильтруем системный стиль (id=1) и применяем поиск
    val visibleStyles = styles.filter { it.id != 1L }.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }
    val favorites = visibleStyles.filter { it.id in favoriteIds }
    val others = visibleStyles.filter { it.id !in favoriteIds }

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Стили",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        SearchField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Поиск стиля...",
        )

        Spacer(Modifier.height(4.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // "Все стили" — сброс
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedStyleId == null,
                    onClick = { onStyleSelect(null) },
                )
                Spacer(Modifier.width(4.dp))
                Text("Все стили", style = MaterialTheme.typography.bodyMedium)
            }

            // Избранные стили
            if (favorites.isNotEmpty()) {
                Text(
                    text = "Избранные",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
                favorites.forEach { style ->
                    StyleItem(
                        style = style,
                        isFavorite = true,
                        isSelected = selectedStyleId == style.id,
                        onSelect = { onStyleSelect(style.id) },
                        onToggleFavorite = { onToggleFavorite(style.id) },
                    )
                }
                Spacer(Modifier.height(4.dp))
            }

            // Остальные стили
            if (others.isNotEmpty()) {
                Text(
                    text = "Все",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            others.forEach { style ->
                StyleItem(
                    style = style,
                    isFavorite = style.id in favoriteIds,
                    isSelected = selectedStyleId == style.id,
                    onSelect = { onStyleSelect(style.id) },
                    onToggleFavorite = { onToggleFavorite(style.id) },
                )
            }
        }
    }
}

@Composable
private fun StyleItem(
    style: StyleData,
    isFavorite: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Spacer(Modifier.width(4.dp))
        Text(
            text = style.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        FavoriteStar(
            isFavorite = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp),
        )
    }
}
