package com.example.ai_image_client.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai_image_client.core.ThemeMode
import com.example.ai_image_client.viewmodel.SettingsScreenModel

@Composable
fun SettingsDialog(
    screenModel: SettingsScreenModel,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val state by screenModel.state.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройки") },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Server URL
                Text("URL сервера:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.serverUrl,
                    onValueChange = { screenModel.setServerUrl(it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.height(16.dp))

                // Theme
                Text("Тема:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                ThemeMode.entries.forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.themeMode == mode,
                            onClick = {
                                screenModel.setThemeMode(mode)
                                onThemeModeChanged(mode)
                            },
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.SYSTEM -> "Системная"
                                ThemeMode.LIGHT -> "Светлая"
                                ThemeMode.DARK -> "Тёмная"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                if (state.saved) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Сохранено!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                screenModel.save()
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        },
    )
}
