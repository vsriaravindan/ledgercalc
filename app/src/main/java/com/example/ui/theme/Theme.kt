package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val BentoDarkColorScheme = darkColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoBackground,
    primaryContainer = BentoSurface,
    onPrimaryContainer = BentoPrimary,
    background = BentoBackground,
    onBackground = BentoOnBackground,
    surface = BentoSurface,
    onSurface = BentoOnBackground,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = BentoOnBackground,
    outline = BentoOutline,
    outlineVariant = BentoOutlineVariant,
    error = BentoDarkError,
    onError = BentoDarkOnError,
    errorContainer = BentoDarkErrorContainer,
    onErrorContainer = BentoDarkOnErrorContainer
)

private val BentoLightColorScheme = BentoDarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) BentoDarkColorScheme else BentoLightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
