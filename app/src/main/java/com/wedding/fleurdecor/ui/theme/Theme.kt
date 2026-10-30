package com.wedding.fleurdecor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wedding.fleurdecor.data.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = CharcoalDark,
    onPrimary = IvoryLight,
    primaryContainer = BlushSoft,
    onPrimaryContainer = CharcoalDark,
    secondary = RoseGold,
    onSecondary = Color.White,
    secondaryContainer = ChampagneWarm,
    onSecondaryContainer = CharcoalDark,
    tertiary = LavenderAccent,
    onTertiary = Color.White,
    background = IvoryBackground,
    onBackground = CharcoalDark,
    surface = IvoryLight,
    onSurface = CharcoalDark,
    surfaceVariant = ChampagneWarm,
    onSurfaceVariant = WarmGray,
    outline = SoftGray.copy(alpha = 0.5f),
    outlineVariant = GlassBorderWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = BlushAccent,
    onPrimary = CharcoalDark,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = TextLight,
    secondary = RoseGold,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = TextLight,
    tertiary = LavenderAccent,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkGlassBorder,
    outlineVariant = DarkGlassBorder
)

@Composable
fun FleurDecorTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
