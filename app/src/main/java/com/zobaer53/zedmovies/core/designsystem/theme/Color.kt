
package com.zobaer53.zedmovies.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightPrimary = Color(0xFFDFD9FF)
private val LightPrimaryVariant = Color(0xFFB2A0D4)
private val LightAccent = Color(0xFF6647F0)
private val LightSecondary = Color(0xFF00993F)
private val LightSecondaryVariant = Color(0xFFE06000)
private val LightError = Color(0xFFB80000)
private val LightTextPrimary = Color(0xFF0E0D0D)
private val LightTextPrimaryVariant = Color(0xFF272727)
private val LightTextSecondary = Color(0xFF4F4F53)

private val DarkPrimary = Color(0xFF1F1D2B)
private val DarkPrimaryVariant = Color(0xFF252836)
private val DarkAccent = Color(0xFF12CDD9)
private val DarkSecondary = Color(0xFF22B07D)
private val DarkSecondaryVariant = Color(0xFFFF8700)
private val DarkError = Color(0xFFFB4141)
private val DarkTextPrimary = Color(0xFFFFFBFF)
private val DarkTextPrimaryVariant = Color(0xFFEBEBEF)
private val DarkTextSecondary = Color(0xFF92929D)

private val TextOnMedia = Color(0xFFFFFBFF)
private val TextOnMediaVariant = Color(0xFFEBEBEF)

internal val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightPrimaryVariant,
    background = LightPrimary,
    surface = LightPrimary
)

internal val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkPrimaryVariant,
    background = DarkPrimary,
    surface = DarkPrimary
)

@Immutable
data class zedMoviesColors(
    val default: Color = Color.Unspecified,
    val primary: Color,
    val primaryVariant: Color,
    val accent: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val error: Color,
    val textPrimary: Color,
    val textPrimaryVariant: Color,
    val textSecondary: Color,
    val textOnMedia: Color,
    val textOnMediaVariant: Color
)

internal val LightzedMoviesColors = zedMoviesColors(
    primary = LightPrimary,
    primaryVariant = LightPrimaryVariant,
    accent = LightAccent,
    secondary = LightSecondary,
    secondaryVariant = LightSecondaryVariant,
    error = LightError,
    textPrimary = LightTextPrimary,
    textPrimaryVariant = LightTextPrimaryVariant,
    textSecondary = LightTextSecondary,
    textOnMedia = TextOnMedia,
    textOnMediaVariant = TextOnMediaVariant
)

internal val DarkzedMoviesColors = zedMoviesColors(
    primary = DarkPrimary,
    primaryVariant = DarkPrimaryVariant,
    accent = DarkAccent,
    secondary = DarkSecondary,
    secondaryVariant = DarkSecondaryVariant,
    error = DarkError,
    textPrimary = DarkTextPrimary,
    textPrimaryVariant = DarkTextPrimaryVariant,
    textSecondary = DarkTextSecondary,
    textOnMedia = TextOnMedia,
    textOnMediaVariant = TextOnMediaVariant
)

internal val LocalzedMoviesColors = staticCompositionLocalOf<zedMoviesColors> {
    error(LocalzedMoviesColorsErrorMessage)
}

private const val LocalzedMoviesColorsErrorMessage = "No zedMoviesColors provided."
