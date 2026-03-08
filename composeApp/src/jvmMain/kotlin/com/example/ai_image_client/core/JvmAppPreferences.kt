package com.example.ai_image_client.core

import java.util.prefs.Preferences

class JvmAppPreferences : AppPreferences {

    private val prefs = Preferences.userNodeForPackage(JvmAppPreferences::class.java)

    override var serverUrl: String
        get() = prefs.get(KEY_SERVER_URL, DEFAULT_SERVER_URL)
        set(value) = prefs.put(KEY_SERVER_URL, value)

    override var themeMode: ThemeMode
        get() = try {
            ThemeMode.valueOf(prefs.get(KEY_THEME_MODE, ThemeMode.SYSTEM.name))
        } catch (_: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
        set(value) = prefs.put(KEY_THEME_MODE, value.name)

    override var sidebarExpanded: Boolean
        get() = prefs.getBoolean(KEY_SIDEBAR_EXPANDED, true)
        set(value) = prefs.putBoolean(KEY_SIDEBAR_EXPANDED, value)

    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_SIDEBAR_EXPANDED = "sidebar_expanded"
        private const val DEFAULT_SERVER_URL = "http://localhost:8080"
    }
}
