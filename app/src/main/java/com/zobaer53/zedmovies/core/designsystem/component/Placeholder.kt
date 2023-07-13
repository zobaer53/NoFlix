

package com.zobaer53.zedmovies.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.google.accompanist.placeholder.material.placeholder
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme

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

fun Modifier.zedMoviesPlaceholder(color: Color) = composed {
    placeholder(
        visible = true,
        color = color,
        shape = zedMoviesTheme.shapes.medium
    )
}
