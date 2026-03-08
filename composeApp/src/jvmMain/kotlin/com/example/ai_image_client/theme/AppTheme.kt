package com.example.ai_image_client.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ai_image_client.core.ThemeMode

// Цвета из документации (03_Навигация_и_общие_паттерны.md §7)
private val LightColorScheme = lightColorScheme(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFFFAFAFA),       // card
    primary = Color(0xFF1976D2),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    onSurfaceVariant = Color(0xFF757575),      // secondary text
    secondaryContainer = Color(0xFFE3F2FD),    // hover / selected
    onSecondaryContainer = Color(0xFF1976D2),
    outline = Color(0xFFBDBDBD),
)

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF1E1E1E),
    surface = Color(0xFF252525),
    surfaceVariant = Color(0xFF2D2D2D),        // card
    primary = Color(0xFF64B5F6),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF9E9E9E),      // secondary text
    secondaryContainer = Color(0xFF37474F),    // hover / selected
    onSecondaryContainer = Color(0xFF64B5F6),
    outline = Color(0xFF424242),
)

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
