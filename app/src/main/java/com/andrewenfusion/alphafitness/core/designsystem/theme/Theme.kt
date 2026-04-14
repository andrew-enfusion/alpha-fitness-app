package com.andrewenfusion.alphafitness.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Blue700,
    secondary = Blue500,
    tertiary = SuccessBlue,
    background = Cloud,
    surface = White,
    surfaceVariant = Blue100,
    onPrimary = White,
    onSecondary = White,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Blue900,
    error = ErrorRed,
    onError = White,
)

private val DarkColors = darkColorScheme(
    primary = Blue200,
    secondary = Blue500,
    tertiary = SuccessBlue,
    background = Blue900,
    surface = ColorTokens.DarkSurface,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onPrimary = Blue900,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = Blue100,
    error = ErrorRed,
    onError = White,
)

private object ColorTokens {
    val DarkSurface = androidx.compose.ui.graphics.Color(0xFF12304A)
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF1A446A)
}

@Composable
fun AlphaFitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AlphaFitnessTypography,
        shapes = AlphaFitnessShapes,
        content = content,
    )
}
