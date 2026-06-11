package com.namvu.myapplication.ui.base.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    secondary = Color(0xFF0F766E),
    onSecondary = Color.White,
    tertiary = Color(0xFF7C3AED),
    error = Color(0xFFB3261E),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    surfaceVariant = Color(0xFFE7EAF0),
    onSurface = Color(0xFF191C20),
    onSurfaceVariant = Color(0xFF42474E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9BB8FF),
    onPrimary = Color(0xFF002F68),
    secondary = Color(0xFF80D5CB),
    onSecondary = Color(0xFF003733),
    tertiary = Color(0xFFD0BCFF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF111318),
    surface = Color(0xFF191C20),
    surfaceVariant = Color(0xFF42474E),
    onSurface = Color(0xFFE2E2E9),
    onSurfaceVariant = Color(0xFFC2C7CF),
)

val LocalAppSpacing = staticCompositionLocalOf { DefaultAppSpacing }
val LocalAppDimens = staticCompositionLocalOf { DefaultAppDimens }

object AppThemeDefaults {
    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val dimens: AppDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppDimens.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
