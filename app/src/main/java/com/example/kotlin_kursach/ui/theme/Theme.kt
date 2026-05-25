package com.example.kotlin_kursach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EduBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E4FF),
    onPrimaryContainer = EduBlueDark,
    secondary = EduTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    background = EduBackground,
    onBackground = EduOnSurface,
    surface = EduSurface,
    onSurface = EduOnSurface,
    surfaceVariant = Color(0xFFE8EEF4),
    onSurfaceVariant = Color(0xFF5A6578),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D3058),
    primaryContainer = EduBlueDark,
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF003731),
    background = Color(0xFF121820),
    onBackground = Color(0xFFE8EEF4),
    surface = Color(0xFF1A2332),
    onSurface = Color(0xFFE8EEF4),
)

@Composable
fun Kotlin_KursachTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
