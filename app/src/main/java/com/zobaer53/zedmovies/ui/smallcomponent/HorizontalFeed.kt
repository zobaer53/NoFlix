
package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesImagePlaceholder
import com.zobaer53.zedmovies.ui.designsystem.component.ZedMoviesNetworkImage
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.R

@Composable
internal fun HorizontalFeedItem(
    title: String,
    posterPath: String?,
    genres: List<String>,
    voteAverage: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = zedMoviesTheme.colors.primaryVariant,
    shape: Shape = zedMoviesTheme.shapes.smallMedium,
    isPlaceholder: Boolean = false
) {
    Card(
        modifier = modifier
            .width(HorizontalFeedItemWidth)
            .clip(shape)
            .then(if (isPlaceholder) Modifier else Modifier.clickable(onClick = onClick)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = shape
    ) {
        Column {
            Box(modifier = Modifier.height(HorizontalFeedItemPosterHeight)) {
                if (isPlaceholder) {
                    zedMoviesImagePlaceholder()
                } else {
                    ZedMoviesNetworkImage(
                        modifier = Modifier.fillMaxSize(),
                        model = posterPath,
                        contentDescription = title
                    )
                }
                RatingItem(
                    "",
                    "",
                    rating = voteAverage,
                    modifier = Modifier
                        .padding(
                            top = zedMoviesTheme.spacing.small,
                            end = zedMoviesTheme.spacing.small
                        )
                        .align(Alignment.TopEnd)
                        .then(
                            if (isPlaceholder) {
                                Modifier.zedMoviesPlaceholder(color = zedMoviesTheme.colors.secondaryVariant)
                            } else {
                                Modifier
                            }
                        )
                )
            }
            Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.smallMedium))
            Text(
                modifier = Modifier
                    .padding(horizontal = zedMoviesTheme.spacing.small)
                    .then(
                        if (isPlaceholder) {
                            Modifier
                                .fillMaxWidth()
                                .zedMoviesPlaceholder(
                                    color = zedMoviesTheme.colors.textPrimary
                                )
                        } else {
                            Modifier
                        }
                    ),
                text = title,
                style = zedMoviesTheme.typography.semiBold.h5,
                color = zedMoviesTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.extraSmall))
            Text(
                modifier = Modifier
                    .padding(
                        start = zedMoviesTheme.spacing.small,
                        end = zedMoviesTheme.spacing.small,
                        bottom = zedMoviesTheme.spacing.small
                    )
                    .then(
                        if (isPlaceholder) {
                            Modifier
                                .fillMaxWidth(
                                    HorizontalFeedItemPlaceholderSecondTextMaxWidthFraction
                                )
                                .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textSecondary)
                        } else {
                            Modifier
                        }
                    ),
                text = genres.joinToString(separator = FeedItemGenreSeparator)
                    .ifEmpty { stringResource(id = R.string.no_genre) },
                style = zedMoviesTheme.typography.medium.h7,
                color = zedMoviesTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun HorizontalFeedItemPlaceholder(modifier: Modifier = Modifier) {
    HorizontalFeedItem(
        modifier = modifier,
        title = FeedItemPlaceholderText,
        posterPath = FeedItemPlaceholderText,
        genres = emptyList(),
        voteAverage = FeedItemPlaceholderRating,
        onClick = {},
        isPlaceholder = true
    )
}

private val HorizontalFeedItemWidth = 135.dp
private val HorizontalFeedItemPosterHeight = 178.dp
private const val HorizontalFeedItemPlaceholderSecondTextMaxWidthFraction = 0.5f
