

package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesCenteredBox
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesOutlinedButton
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asString
import com.zobaer53.zedmovies.R

@Composable
fun zedMoviesError(
    errorMessage: UserMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.medium,
    containerColor: Color = zedMoviesTheme.colors.primaryVariant,
    errorTextColor: Color = zedMoviesTheme.colors.error,
    actionButtonColor: Color = zedMoviesTheme.colors.accent,
    errorTextStyle: TextStyle = zedMoviesTheme.typography.regular.h4,
    @StringRes actionButtonTextResourceId: Int = R.string.retry,
    shouldShowOfflineMode: Boolean = false,
    onOfflineModeClick: () -> Unit = {},
    offlineModeButtonColor: Color = zedMoviesTheme.colors.secondary,
    @StringRes offlineModeButtonTextResourceId: Int = R.string.offline_mode
) {
    Column(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)
    ) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Column(
                modifier = Modifier.padding(zedMoviesTheme.spacing.extraMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)
            ) {
                Text(
                    text = errorMessage.asString(),
                    style = errorTextStyle,
                    color = errorTextColor
                )
                zedMoviesOutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    textResourceId = actionButtonTextResourceId,
                    onClick = onRetry,
                    containerColor = zedMoviesTheme.colors.primaryVariant,
                    contentColor = actionButtonColor
                )
            }
        }
        if (shouldShowOfflineMode) {
            zedMoviesOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                textResourceId = offlineModeButtonTextResourceId,
                onClick = onOfflineModeClick,
                containerColor = zedMoviesTheme.colors.primary,
                contentColor = offlineModeButtonColor
            )
        }
    }
}

@Composable
fun zedMoviesCenteredError(
    errorMessage: UserMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.medium,
    containerColor: Color = zedMoviesTheme.colors.primaryVariant,
    errorTextColor: Color = zedMoviesTheme.colors.error,
    actionButtonColor: Color = zedMoviesTheme.colors.accent,
    @StringRes actionButtonTextResourceId: Int = R.string.retry,
    shouldShowOfflineMode: Boolean = false,
    onOfflineModeClick: () -> Unit = {},
    offlineModeButtonColor: Color = zedMoviesTheme.colors.secondary,
    @StringRes offlineModeButtonTextResourceId: Int = R.string.offline_mode
) {
    zedMoviesCenteredBox(
        modifier = modifier
            .padding(horizontal = zedMoviesTheme.spacing.extraMedium)
            .fillMaxSize()
    ) {
        zedMoviesError(
            errorMessage = errorMessage,
            onRetry = onRetry,
            shape = shape,
            containerColor = containerColor,
            errorTextColor = errorTextColor,
            actionButtonColor = actionButtonColor,
            actionButtonTextResourceId = actionButtonTextResourceId,
            shouldShowOfflineMode = shouldShowOfflineMode,
            onOfflineModeClick = onOfflineModeClick,
            offlineModeButtonColor = offlineModeButtonColor,
            offlineModeButtonTextResourceId = offlineModeButtonTextResourceId
        )
    }
}
