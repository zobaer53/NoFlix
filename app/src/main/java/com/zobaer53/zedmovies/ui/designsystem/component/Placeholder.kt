package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun zedMoviesImagePlaceholder(
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.primaryVariant
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zedMoviesPlaceholder(color = color)
    )
}

fun Modifier.zedMoviesPlaceholder() = composed {
    zedMoviesPlaceholder(color = zedMoviesTheme.colors.primaryVariant)
}

/** Draws a [color] shape in place of the content while it is loading. */
fun Modifier.zedMoviesPlaceholder(color: Color) = composed {
    val shape = zedMoviesTheme.shapes.medium
    drawWithContent {
        drawOutline(outline = shape.createOutline(size, layoutDirection, this), color = color)
    }
}
