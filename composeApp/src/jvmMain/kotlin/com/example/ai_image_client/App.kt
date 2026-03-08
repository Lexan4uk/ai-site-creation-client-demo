package com.example.ai_image_client

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.koin.compose.koinInject
import com.example.ai_image_client.core.AppPreferences
import com.example.ai_image_client.core.ThemeMode
import com.example.ai_image_client.navigation.AppSidebar
import com.example.ai_image_client.navigation.GalleryTab
import com.example.ai_image_client.navigation.TabContentScreen
import com.example.ai_image_client.theme.AppTheme
import com.example.ai_image_client.ui.common.Footer
import com.example.ai_image_client.ui.common.Header
import com.example.ai_image_client.ui.common.SettingsDialog
import com.example.ai_image_client.viewmodel.SettingsScreenModel

@Composable
fun App() {
    val preferences = koinInject<AppPreferences>()
    var themeMode by remember { mutableStateOf(preferences.themeMode) }
    var showSettings by remember { mutableStateOf(false) }
    var sidebarExpanded by remember { mutableStateOf(preferences.sidebarExpanded) }

    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    AppTheme(themeMode) {
        TabNavigator(GalleryTab) {
            Navigator(TabContentScreen) { navigator ->
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header
                        Header(
                            isDark = isDark,
                            onThemeToggle = {
                                val newMode = if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                                themeMode = newMode
                                preferences.themeMode = newMode
                            },
                            onSettingsClick = { showSettings = true },
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Main content: sidebar + screen
                        Row(modifier = Modifier.weight(1f)) {
                            AppSidebar(
                                expanded = sidebarExpanded,
                                onToggle = {
                                    sidebarExpanded = !sidebarExpanded
                                    preferences.sidebarExpanded = sidebarExpanded
                                },
                                onNavigate = { navigator.popUntilRoot() },
                            )
                            CurrentScreen()
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Footer
                        Footer()
                    }
                }
            }
        }

        // Settings dialog
        if (showSettings) {
            val settingsScreenModel = koinInject<SettingsScreenModel>()
            SettingsDialog(
                screenModel = settingsScreenModel,
                onThemeModeChanged = { mode ->
                    themeMode = mode
                },
                onDismiss = { showSettings = false },
            )
        }
    }
}
