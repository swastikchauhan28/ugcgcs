package com.example.ugcgcs.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GcsColors = darkColorScheme(
    primary = Color(0xFF70D6FF),
    secondary = Color(0xFF83E8B3),
    tertiary = Color(0xFFFFD166),
    background = Color(0xFF0A1015),
    surface = Color(0xFF101A22),
    surfaceVariant = Color(0xFF172630),
    onBackground = Color(0xFFE7F0F5),
    onSurface = Color(0xFFE7F0F5),
    error = Color(0xFFFF6B6B)
)

@Composable
fun UgcGcsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = GcsColors, content = content)
}
