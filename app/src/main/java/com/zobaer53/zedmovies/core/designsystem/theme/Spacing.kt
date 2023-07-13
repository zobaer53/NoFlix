package com.zobaer53.zedmovies.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultSpace = 0.dp
private val ExtraSmallSpace = 4.dp
private val SmallSpace = 8.dp
private val SmallMediumSpace = 12.dp
private val MediumSpace = 16.dp
private val ExtraMediumSpace = 24.dp
private val LargeSpace = 32.dp
private val ExtraLargeSpace = 40.dp
private val LargestSpace = 64.dp

@Immutable
data class zedMoviesSpacing(
    val default: Dp = DefaultSpace,
    val extraSmall: Dp = ExtraSmallSpace,
    val small: Dp = SmallSpace,
    val smallMedium: Dp = SmallMediumSpace,
    val medium: Dp = MediumSpace,
    val extraMedium: Dp = ExtraMediumSpace,
    val large: Dp = LargeSpace,
    val extraLarge: Dp = ExtraLargeSpace,
    val largest: Dp = LargestSpace
)

internal val LocalzedMoviesSpacing = staticCompositionLocalOf { zedMoviesSpacing() }
