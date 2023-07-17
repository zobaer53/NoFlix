

package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun zedMoviesSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.small,
    containerColor: Color = zedMoviesTheme.colors.primaryVariant,
    contentColor: Color = zedMoviesTheme.colors.textPrimaryVariant,
    actionColor: Color = zedMoviesTheme.colors.accent
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    ) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = actionColor
        )
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error(LocalSnackbarHostStateErrorMessage)
}

private const val LocalSnackbarHostStateErrorMessage = "No SnackbarHostState provided."
