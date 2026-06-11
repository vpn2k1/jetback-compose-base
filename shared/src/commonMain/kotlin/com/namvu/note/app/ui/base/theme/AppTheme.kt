package com.namvu.note.app.ui.base.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = Color(0xFF22C55E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF14532D),
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    error = Color(0xFFEF4444),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    surfaceVariant = Color(0xFFEFF2F7),
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF86EFAC),
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF166534),
    onSecondaryContainer = Color(0xFFDCFCE7),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    error = Color(0xFFFCA5A5),
    background = Color(0xFF0F172A),
    surface = Color(0xFF111827),
    surfaceVariant = Color(0xFF243244),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
)

private val AppTypography = Typography().let { base ->
    base.copy(
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold),
        headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.Bold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

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
    themeMode: AppThemeMode = AppThemeMode.System,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        AppThemeMode.System -> darkTheme
        AppThemeMode.Light -> false
        AppThemeMode.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
