package com.example.calchub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = NeoBlack,
    secondary = NeonPink,
    onSecondary = NeoBlack,
    tertiary = NeonCyan,
    onTertiary = NeoBlack,
    background = NeonBackground,
    onBackground = NeonText,
    surface = NeonSurface,
    onSurface = NeonText,
    surfaceVariant = NeonBorder,
    onSurfaceVariant = NeonTextSecondary,
    outline = NeonGreen
)

// NeoPOP is predominantly dark, so we use the same scheme for light mode 
// or a high-contrast version. For now, let's enforce the dark aesthetic.
private val LightColorScheme = DarkColorScheme

/**
 * Custom Theme for CalcHub application.
 * Wraps [MaterialTheme] with specific colors and typography.
 *
 * @param darkTheme Whether to use dark theme (default: system setting).
 * @param dynamicColor Whether to use dynamic color (Android 12+) (default: false to enforce NeoPop).
 * @param content The content to display inside the theme.
 */
@Composable
fun CalcHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // We default to false to enforce NeoPOP branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
