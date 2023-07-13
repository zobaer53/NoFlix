package com.zobaer53.zedmovies.core.designsystem.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme

@Composable
fun zedMoviesCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.accent,
    strokeWidth: Dp = StrokeWidth
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth
    )
}

private val StrokeWidth = 2.dp
