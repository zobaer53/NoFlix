package com.zobaer53.zedmovies.ui.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val DefaultShape = RectangleShape
private val ExtraSmallShape = RoundedCornerShape(4.dp)
private val SmallShape = RoundedCornerShape(8.dp)
private val SmallMediumShape = RoundedCornerShape(12.dp)
private val MediumShape = RoundedCornerShape(16.dp)
private val ExtraMediumShape = RoundedCornerShape(24.dp)
private val LargeShape = RoundedCornerShape(32.dp)
private val ExtraLargeShape = RoundedCornerShape(40.dp)

internal val Shapes = Shapes(small = SmallShape, medium = MediumShape, large = LargeShape)

@Immutable
data class zedMoviesShapes(
    val default: Shape = DefaultShape,
    val extraSmall: Shape = ExtraSmallShape,
    val small: Shape = SmallShape,
    val smallMedium: Shape = SmallMediumShape,
    val medium: Shape = MediumShape,
    val extraMedium: Shape = ExtraMediumShape,
    val large: Shape = LargeShape,
    val extraLarge: Shape = ExtraLargeShape
)

internal val LocalzedMoviesShapes = staticCompositionLocalOf { zedMoviesShapes() }
