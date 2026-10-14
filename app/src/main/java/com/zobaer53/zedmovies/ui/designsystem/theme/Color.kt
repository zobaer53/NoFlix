
package com.zobaer53.zedmovies.ui.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Cool neutrals with the brand's cyan (from the launcher icon), darkened to #007C86 so it reads
// as text on the light background (4.6:1).
private val LightPrimary = Color(0xFFF5F6FA)
private val LightPrimaryVariant = Color(0xFFE4E7F0)
private val LightAccent = Color(0xFF007C86)
private val LightAccentContainer = Color(0xFFD5EDEF)
private val LightSecondary = Color(0xFF00875A)
private val LightSecondaryVariant = Color(0xFFFFB020)
private val LightError = Color(0xFFC62828)
private val LightTextPrimary = Color(0xFF12141C)
private val LightTextPrimaryVariant = Color(0xFF2A2D38)
private val LightTextSecondary = Color(0xFF565C6B)

private val DarkPrimary = Color(0xFF1F1D2B)
private val DarkPrimaryVariant = Color(0xFF252836)
private val DarkAccent = Color(0xFF12CDD9)
private val DarkAccentContainer = Color(0xFF173A45)
private val DarkSecondary = Color(0xFF22B07D)
private val DarkSecondaryVariant = Color(0xFFFFB020)
private val DarkError = Color(0xFFFB4141)
private val DarkTextPrimary = Color(0xFFFFFBFF)
private val DarkTextPrimaryVariant = Color(0xFFEBEBEF)
private val DarkTextSecondary = Color(0xFF92929D)

/** Behind chips drawn on posters and backdrops, whatever the theme. */
private val MediaScrim = Color(0x99000000)
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
    /** A tinted surface for selected states, such as the bottom bar's current tab. */
    val accentContainer: Color,
    val secondary: Color,
    /** Gold, for ratings; drawn on [mediaScrim]. */
    val secondaryVariant: Color,
    val error: Color,
    val textPrimary: Color,
    val textPrimaryVariant: Color,
    val textSecondary: Color,
    val textOnMedia: Color,
    val textOnMediaVariant: Color,
    val mediaScrim: Color = MediaScrim
)

internal val LightzedMoviesColors = zedMoviesColors(
    primary = LightPrimary,
    primaryVariant = LightPrimaryVariant,
    accent = LightAccent,
    accentContainer = LightAccentContainer,
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
    accentContainer = DarkAccentContainer,
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
