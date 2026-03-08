package com.example.ai_image_client.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator

object TabContentScreen : Screen {

    private fun readResolve(): Any = TabContentScreen

    @Composable
    override fun Content() {
        val tabNavigator = LocalTabNavigator.current
        tabNavigator.current.Content()
    }
}
