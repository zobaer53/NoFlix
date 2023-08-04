package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesImagePlaceholder
import com.zobaer53.zedmovies.ui.designsystem.component.ZedMoviesNetworkImage
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.data.model.ReleaseDate
import com.zobaer53.zedmovies.R

@Composable
internal fun VerticalFeedItem(
    title: String,
    overview: String,
    posterPath: String?,
    voteAverage: Double,
    releaseDate: ReleaseDate,
    genres: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.small,
    isPlaceholder: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(VerticalFeedItemHeight)
            .clip(shape)
            .then(if (isPlaceholder) Modifier else Modifier.clickable(onClick = onClick)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .width(VerticalFeedItemPosterWidth)
                .fillMaxHeight()
                .clip(shape)
        ) {
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
                        start = zedMoviesTheme.spacing.small
                    )
                    .align(Alignment.TopStart)
                    .then(
                        if (isPlaceholder) {
                            Modifier.zedMoviesPlaceholder(color = zedMoviesTheme.colors.secondaryVariant)
                        } else {
                            Modifier
                        }
                    )
            )
        }
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = if (isPlaceholder) {
                    Modifier
                        .fillMaxWidth()
                        .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textPrimaryVariant)
                } else {
                    Modifier
                },
                text = title,
                style = zedMoviesTheme.typography.semiBold.h4,
                color = zedMoviesTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                style = zedMoviesTheme.typography.medium.h5,
                color = zedMoviesTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Column {
                if (isPlaceholder) {
                    VerticalFeedItemIconAndTextPlaceholder(iconResourceId = R.drawable.ic_calendar)
                    VerticalFeedItemIconAndTextPlaceholder(iconResourceId = R.drawable.ic_film)
                } else {
                    VerticalFeedItemIconAndText(
                        iconResourceId = R.drawable.ic_calendar,
                        text = releaseDate.year.ifEmpty { stringResource(id = R.string.no_release_date) }
                    )
                    VerticalFeedItemIconAndText(
                        iconResourceId = R.drawable.ic_film,
                        text = genres.joinToString(separator = FeedItemGenreSeparator)
                            .ifEmpty { stringResource(id = R.string.no_genre) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun VerticalFeedItemPlaceholder(modifier: Modifier = Modifier) {
    VerticalFeedItem(
        modifier = modifier,
        title = FeedItemPlaceholderText,
        overview = FeedItemPlaceholderText,
        posterPath = FeedItemPlaceholderText,
        voteAverage = FeedItemPlaceholderRating,
        releaseDate = ReleaseDate(),
        genres = emptyList(),
        onClick = {},
        isPlaceholder = true
    )
}

@Composable
private fun VerticalFeedItemIconAndText(
    @DrawableRes iconResourceId: Int,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.textSecondary,
    isPlaceholder: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraSmall)
    ) {
        Icon(
            modifier = Modifier.size(VerticalFeedItemIconSize),
            painter = painterResource(id = iconResourceId),
            contentDescription = text,
            tint = color
        )
        Text(
            modifier = if (isPlaceholder) {
                Modifier
                    .fillMaxWidth()
                    .height(VerticalFeedItemIconAndTextPlaceholderHeight)
                    .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textSecondary)
            } else {
                Modifier
            },
            text = text,
            style = zedMoviesTheme.typography.medium.h5,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun VerticalFeedItemIconAndTextPlaceholder(
    @DrawableRes iconResourceId: Int,
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.textSecondary
) {
    VerticalFeedItemIconAndText(
        iconResourceId = iconResourceId,
        text = FeedItemPlaceholderText,
        modifier = modifier,
        color = color,
        isPlaceholder = true
    )
}

private val VerticalFeedItemHeight = 147.dp
private val VerticalFeedItemPosterWidth = 112.dp
private val VerticalFeedItemIconSize = 18.dp
private val VerticalFeedItemIconAndTextPlaceholderHeight = VerticalFeedItemIconSize / 1.5f
