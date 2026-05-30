package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = SoftWhite,
    primaryContainer = SpaceDeck,
    secondary = NeonPurple,
    onSecondary = SoftWhite,
    tertiary = LaserCyan,
    onTertiary = DeepVoid,
    background = DeepVoid,
    onBackground = SoftWhite,
    surface = SpaceCard,
    onSurface = SoftWhite,
    surfaceVariant = SpaceDeck,
    onSurfaceVariant = MutedSlate,
    error = NeonCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Force the stunning Cyber Scheme for absolute visual immersive experience
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
