package com.example.ai_image_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.context.startKoin
import com.example.ai_image_client.di.appModule
import com.example.ai_image_client.di.repositoryModule
import com.example.ai_image_client.di.screenModelModule

fun main() {
    startKoin {
        modules(appModule, repositoryModule, screenModelModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "AI Image Library",
            state = rememberWindowState(placement = WindowPlacement.Maximized),
        ) {
            App()
        }
    }
}
