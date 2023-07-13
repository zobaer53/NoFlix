

package com.zobaer53.zedmovies.feature.details.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.core.designsystem.component.ZedMoviesCardNetworkImage
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesImagePlaceholder
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.core.model.Credits
import com.zobaer53.zedmovies.R

@Composable
internal fun CastAndCrew(
    credits: Credits,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false
) {
    val cast = credits.cast
    val crew = credits.crew

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)
    ) {
        if (isPlaceholder) {
            val placeholderContent: LazyListScope.() -> Unit = {
                items(PlaceholderCount) {
                    CastAndCrewItemPlaceholder()
                }
            }
            CastAndCrewContainer(titleResourceId = R.string.cast, content = placeholderContent)
            CastAndCrewContainer(titleResourceId = R.string.crew, content = placeholderContent)
        } else {
            if (cast.isNotEmpty()) {
                CastAndCrewContainer(titleResourceId = R.string.cast) {
                    items(cast) { castItem ->
                        with(castItem) {
                            CastAndCrewItem(
                                profilePath = profilePath,
                                name = name,
                                description = character
                            )
                        }
                    }
                }
            }
            if (crew.isNotEmpty()) {
                CastAndCrewContainer(titleResourceId = R.string.crew) {
                    items(crew) { crewItem ->
                        with(crewItem) {
                            CastAndCrewItem(
                                profilePath = profilePath,
                                name = name,
                                description = job
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CastAndCrewPlaceholder(modifier: Modifier = Modifier) {
    CastAndCrew(
        modifier = modifier,
        credits = Credits(cast = emptyList(), crew = emptyList()),
        isPlaceholder = true
    )
}

@Suppress("ModifierParameterPosition", "ComposableParametersOrdering")
@Composable
private fun CastAndCrewContainer(
    @StringRes titleResourceId: Int,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = zedMoviesTheme.spacing.extraMedium),
            text = stringResource(id = titleResourceId),
            style = zedMoviesTheme.typography.semiBold.h4,
            color = zedMoviesTheme.colors.textPrimary
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.smallMedium),
            contentPadding = PaddingValues(horizontal = zedMoviesTheme.spacing.extraMedium),
            content = content
        )
    }
}

@Composable
private fun CastAndCrewItem(
    profilePath: String?,
    name: String,
    description: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    isPlaceholder: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)
    ) {
        if (isPlaceholder) {
            zedMoviesImagePlaceholder(
                modifier = Modifier
                    .size(CastAndCrewItemImageSize)
                    .clip(shape)
            )
        } else {
            ZedMoviesCardNetworkImage(
                modifier = Modifier.size(CastAndCrewItemImageSize),
                model = profilePath,
                contentDescription = name,
                shape = shape
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraSmall)) {
            Text(
                modifier = if (isPlaceholder) {
                    Modifier
                        .width(PlaceholderTextWidth)
                        .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textSecondary)
                } else {
                    Modifier
                },
                text = name,
                style = zedMoviesTheme.typography.semiBold.h5,
                color = zedMoviesTheme.colors.textPrimary
            )
            Text(
                modifier = if (isPlaceholder) {
                    Modifier
                        .width(PlaceholderTextWidth)
                        .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textSecondary)
                } else {
                    Modifier
                },
                text = description,
                style = zedMoviesTheme.typography.medium.h7,
                color = zedMoviesTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun CastAndCrewItemPlaceholder(modifier: Modifier = Modifier) {
    CastAndCrewItem(
        modifier = modifier,
        profilePath = null,
        name = PlaceholderText,
        description = PlaceholderText,
        isPlaceholder = true
    )
}

private val CastAndCrewItemImageSize = 40.dp
private val PlaceholderTextWidth = 50.dp
private const val PlaceholderText = ""
private const val PlaceholderCount = 20
