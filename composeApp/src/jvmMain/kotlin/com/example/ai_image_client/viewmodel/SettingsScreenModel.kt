package com.example.ai_image_client.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.ai_image_client.core.AppPreferences
import com.example.ai_image_client.core.ThemeMode

data class SettingsState(
    val serverUrl: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val saved: Boolean = false,
)

class SettingsScreenModel(
    private val preferences: AppPreferences,
) : ScreenModel {

    private val _state = MutableStateFlow(
        SettingsState(
            serverUrl = preferences.serverUrl,
            themeMode = preferences.themeMode,
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun setServerUrl(url: String) {
        _state.update { it.copy(serverUrl = url, saved = false) }
    }

    fun setThemeMode(mode: ThemeMode) {
        _state.update { it.copy(themeMode = mode, saved = false) }
    }

    fun save() {
        val s = _state.value
        preferences.serverUrl = s.serverUrl.trim()
        preferences.themeMode = s.themeMode
        _state.update { it.copy(saved = true) }
    }
}
