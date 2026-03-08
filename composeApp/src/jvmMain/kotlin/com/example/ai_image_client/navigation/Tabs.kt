package com.example.ai_image_client.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.ai_image_client.ui.gallery.GalleryContent
import com.example.ai_image_client.ui.generation.GenerationContent
import com.example.ai_image_client.viewmodel.GalleryScreenModel
import com.example.ai_image_client.viewmodel.GenerationScreenModel

object GalleryTab : Tab {
    private fun readResolve(): Any = GalleryTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Image)
            return remember { TabOptions(index = 0u, title = "Галерея", icon = icon) }
        }

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<GalleryScreenModel>()
        GalleryContent(screenModel)
    }
}

object GenerationTab : Tab {
    private fun readResolve(): Any = GenerationTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.AutoAwesome)
            return remember { TabOptions(index = 1u, title = "Генерация", icon = icon) }
        }

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<GenerationScreenModel>()
        GenerationContent(screenModel)
    }
}
