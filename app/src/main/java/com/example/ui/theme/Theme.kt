package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArcadeDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = NeonGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4A3400),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = NeonCrimson,
    onTertiary = Color.White,
    background = DarkArcadeBg,
    onBackground = TextLight,
    surface = DarkArcadeSurface,
    onSurface = TextLight,
    surfaceVariant = DarkArcadeCard,
    onSurfaceVariant = TextMuted
)

@Composable
fun ApexStrikerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcadeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
