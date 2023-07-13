

package com.zobaer53.zedmovies.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesIconButton
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.R

@Composable
fun zedMoviesBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = zedMoviesTheme.colors.textPrimary
) {
    zedMoviesIconButton(
        modifier = modifier
            .size(zedMoviesBackButtonShapeSize)
            .background(
                color = zedMoviesTheme.colors.primaryVariant,
                shape = zedMoviesTheme.shapes.smallMedium
            ),
        iconResourceId = R.drawable.ic_arrow_back,
        contentDescription = stringResource(id = R.string.back),
        onClick = onClick,
        tint = tint
    )
}

private val zedMoviesBackButtonShapeSize = 32.dp
