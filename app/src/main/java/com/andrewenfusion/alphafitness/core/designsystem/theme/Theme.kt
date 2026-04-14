package com.andrewenfusion.alphafitness.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Forest,
    secondary = Sage,
    tertiary = Ember,
    background = Sand,
    surface = Mist,
    onPrimary = Sand,
    onSecondary = Sand,
    onBackground = Slate,
    onSurface = Slate,
)

private val DarkColors = darkColorScheme(
    primary = Sage,
    secondary = Forest,
    tertiary = Ember,
    background = Slate,
    surface = ColorTokens.DarkSurface,
    onPrimary = Slate,
    onSecondary = Sand,
    onBackground = Sand,
    onSurface = Sand,
)

private object ColorTokens {
    val DarkSurface = androidx.compose.ui.graphics.Color(0xFF1E2D2F)
}

@Composable
fun AlphaFitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AlphaFitnessTypography,
        content = content,
    )
}
