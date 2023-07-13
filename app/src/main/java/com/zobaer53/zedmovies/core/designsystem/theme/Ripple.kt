
@file:Suppress("MatchingDeclarationName")

package com.zobaer53.zedmovies.core.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Immutable
internal object zedMoviesRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = RippleColor

    @Composable
    override fun rippleAlpha() = RippleAlpha
}

@Composable
internal fun rememberzedMoviesRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = RippleColor
) = rememberRipple(bounded = bounded, radius = radius, color = color)

private val RippleColor @Composable get() = zedMoviesTheme.colors.accent

private val RippleAlpha = RippleAlpha(
    draggedAlpha = 0.16f,
    focusedAlpha = 0.12f,
    hoveredAlpha = 0.08f,
    pressedAlpha = 0.12f
)
