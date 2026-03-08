package com.example.ai_image_client.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.ui.common.PlaceholderBox
import com.example.ai_image_client.viewmodel.GalleryScreenModel

@Composable
fun GalleryContent(screenModel: GalleryScreenModel) {
    val state by screenModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Левая панель: типы + стили
        Column(modifier = Modifier.width(280.dp)) {
            TypeSelector(
                types = state.types,
                favoriteIds = state.favoriteTypeIds,
                selectedType = state.selectedType,
                searchQuery = state.typeSearchQuery,
                onSearchChange = { screenModel.searchTypes(it) },
                onTypeClick = { screenModel.selectType(it) },
                onToggleFavorite = { screenModel.toggleFavoriteType(it) },
                modifier = Modifier.weight(1f),
            )

            if (state.selectedType != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                StyleFilter(
                    styles = state.styles,
                    favoriteIds = state.favoriteStyleIds,
                    selectedStyleId = state.selectedStyleId,
                    searchQuery = state.styleSearchQuery,
                    onSearchChange = { screenModel.searchStyles(it) },
                    onStyleSelect = { screenModel.selectStyle(it) },
                    onToggleFavorite = { screenModel.toggleFavoriteStyle(it) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Правая часть: галерея или placeholder
        if (state.selectedType != null) {
            AssetGrid(
                assets = state.assets,
                stylesMap = state.stylesMap,
                hasMorePages = state.hasMorePages,
                isLoadingMore = state.isLoadingMore,
                onAssetClick = { screenModel.openViewer(it) },
                onDownload = { screenModel.downloadAsset(it) },
                onLoadMore = { screenModel.loadMoreAssets() },
                modifier = Modifier.weight(1f),
            )
        } else {
            PlaceholderBox(
                text = "Выберите тип изображения",
                modifier = Modifier.weight(1f),
            )
        }
    }

    // Lightbox
    state.viewerAsset?.let { asset ->
        ImageViewerDialog(
            asset = asset,
            stylesMap = state.stylesMap,
            onDismiss = { screenModel.closeViewer() },
        )
    }
}
