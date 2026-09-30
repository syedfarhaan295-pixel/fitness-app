package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitPulseDarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DarkBackground,
    primaryContainer = NeonGreenDark.copy(alpha = 0.25f),
    onPrimaryContainer = NeonGreenLight,
    
    secondary = ElectricCyan,
    onSecondary = DarkBackground,
    secondaryContainer = ElectricCyanDark.copy(alpha = 0.25f),
    onSecondaryContainer = ElectricCyanLight,

    tertiary = CalorieOrange,
    onTertiary = DarkBackground,
    tertiaryContainer = CalorieOrange.copy(alpha = 0.25f),
    onTertiaryContainer = CalorieOrangeLight,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DarkBorder,
    outlineVariant = DarkBorderHighlight,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force modern dark-mode aesthetic as requested
    content: @Composable () -> Unit
) {
    val colorScheme = FitPulseDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
