package com.wafa.signal555.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SignalColorScheme = lightColorScheme(
    primary = SignalRed,
    onPrimary = SignalCard,
    secondary = SignalBlack,
    onSecondary = SignalCard,
    background = SignalSurface,
    onBackground = SignalBlack,
    surface = SignalCard,
    onSurface = SignalBlack,
    outline = SignalBorder
)

@Composable
fun Signal555Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SignalColorScheme,
        content = content
    )
}
