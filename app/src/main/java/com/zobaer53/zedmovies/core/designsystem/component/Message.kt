

package com.zobaer53.zedmovies.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme

@Composable
fun zedMoviesMessage(
    @StringRes messageResourceId: Int,
    @DrawableRes imageResourceId: Int,
    modifier: Modifier = Modifier
) {
    zedMoviesCenteredBox(modifier = modifier.padding(horizontal = zedMoviesTheme.spacing.largest)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium)
        ) {
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = stringResource(id = messageResourceId)
            )
            Text(
                text = stringResource(id = messageResourceId),
                style = zedMoviesTheme.typography.medium.h3,
                color = zedMoviesTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
