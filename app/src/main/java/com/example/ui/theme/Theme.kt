package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GeometricColorScheme = lightColorScheme(
    primary = GeoPrimary,
    onPrimary = GeoOnPrimary,
    primaryContainer = GeoPrimaryContainer,
    onPrimaryContainer = GeoOnPrimaryContainer,
    secondary = GeoRedAccent,
    onSecondary = Color.White,
    secondaryContainer = GeoRedContainer,
    onSecondaryContainer = GeoOnRedContainer,
    background = GeoBackground,
    onBackground = GeoTextMain,
    surface = GeoSurface,
    onSurface = GeoTextMain,
    surfaceVariant = GeoSurfaceVariant,
    onSurfaceVariant = GeoTextMuted,
    outline = GeoOutline
)

@Composable
fun ChekunetsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GeometricColorScheme,
        typography = Typography,
        content = content
    )
}
