@file:Suppress("MatchingDeclarationName")

package com.zobaer53.zedmovies.ui.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Indication for clickable() and other foundation components. */
@Composable
internal fun rememberzedMoviesRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = RippleColor
) = ripple(bounded = bounded, radius = radius, color = color)

/** Ripple color and alpha for Material 3 components (buttons, tabs, …). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun zedMoviesRippleConfiguration() =
    RippleConfiguration(color = RippleColor, rippleAlpha = RippleAlpha)

private val RippleColor @Composable get() = zedMoviesTheme.colors.accent

private val RippleAlpha = RippleAlpha(
    draggedAlpha = 0.16f,
    focusedAlpha = 0.12f,
    hoveredAlpha = 0.08f,
    pressedAlpha = 0.12f
)
