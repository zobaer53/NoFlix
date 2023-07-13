

package com.zobaer53.zedmovies.feature.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.R

@Composable
internal fun Overview(
    overview: String,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(horizontal = zedMoviesTheme.spacing.extraMedium)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)
    ) {
        Text(
            text = stringResource(id = R.string.overview),
            style = zedMoviesTheme.typography.semiBold.h4,
            color = zedMoviesTheme.colors.textPrimary
        )
        Text(
            modifier = if (isPlaceholder) {
                Modifier
                    .fillMaxWidth()
                    .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textSecondary)
            } else {
                Modifier
            },
            text = overview.ifEmpty { stringResource(id = R.string.no_overview) },
            style = zedMoviesTheme.typography.regular.h5,
            color = zedMoviesTheme.colors.textPrimaryVariant
        )
    }
}

@Composable
internal fun OverviewPlaceholder(modifier: Modifier = Modifier) {
    Overview(modifier = modifier, overview = PlaceholderText, isPlaceholder = true)
}

private const val PlaceholderText = ""
