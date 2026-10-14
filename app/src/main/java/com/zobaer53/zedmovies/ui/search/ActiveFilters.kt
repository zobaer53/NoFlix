package com.zobaer53.zedmovies.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.data.model.ActiveFilter
import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesIcon
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesIconButton
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesOutlinedButton
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

/** Opens the filters sheet; the badge counts the active filters. */
@Composable
internal fun FiltersButton(activeCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (activeCount > 0) {
                Badge(
                    containerColor = zedMoviesTheme.colors.accent,
                    contentColor = zedMoviesTheme.colors.primary
                ) { Text(text = activeCount.toString()) }
            }
        }
    ) {
        zedMoviesIconButton(
            iconResourceId = R.drawable.ic_filter,
            contentDescription = stringResource(id = R.string.filters),
            onClick = onClick,
            tint = if (activeCount > 0) zedMoviesTheme.colors.accent else zedMoviesTheme.colors.textSecondary
        )
    }
}

/** One chip per active filter (tap to remove it), then "Clear all". */
@Composable
internal fun ActiveFiltersRow(
    filters: List<ActiveFilter>,
    onRemove: (ActiveFilter) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = zedMoviesTheme.spacing.extraMedium),
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = true,
                onClick = { onRemove(filter) },
                label = { Text(text = filter.label(), style = zedMoviesTheme.typography.medium.h5) },
                trailingIcon = {
                    zedMoviesIcon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(id = R.string.filters_remove)
                    )
                },
                shape = zedMoviesTheme.shapes.small,
                colors = filterChipColors(),
                border = null
            )
        }
        item {
            TextButton(onClick = onClearAll) {
                Text(
                    text = stringResource(id = R.string.filters_clear_all),
                    style = zedMoviesTheme.typography.medium.h5,
                    color = zedMoviesTheme.colors.accent
                )
            }
        }
    }
}

@Composable
private fun ActiveFilter.label(): String = when (this) {
    is ActiveFilter.Sort -> stringResource(id = sort.labelResourceId)
    is ActiveFilter.Genre -> genre.displayName()
    is ActiveFilter.Years -> yearsLabel(from, to)
    is ActiveFilter.Rating -> stringResource(id = R.string.filters_rating_value, formatRating(minRating))
    is ActiveFilter.Language -> languageName(code)
    is ActiveFilter.MinVotes -> stringResource(id = R.string.filters_votes_chip, formatVotes(minVotes))
    is ActiveFilter.Runtime -> runtimeLabel(min, max)
    is ActiveFilter.Country -> countryName(code)
    is ActiveFilter.ExcludedGenre -> stringResource(id = R.string.filters_excluded_genre_chip, genre.displayName())
    is ActiveFilter.Certification -> stringResource(id = R.string.filters_age_rating_chip, code)
    is ActiveFilter.Provider -> provider.name
    is ActiveFilter.Network -> network.displayName
    is ActiveFilter.TvStatus -> stringResource(id = status.labelResourceId)
    is ActiveFilter.TvType -> stringResource(id = type.labelResourceId)
}

/** Empty results while filters are set, or a tab whose media type lacks the selected genres. */
@Composable
internal fun NoFilterResults(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    unmatchableGenres: List<FilterGenre> = emptyList(),
    isTvShows: Boolean = false
) {
    val message = if (unmatchableGenres.isEmpty()) {
        stringResource(id = R.string.filters_no_results)
    } else {
        val names = unmatchableGenres.sorted().map { it.displayName() }.joinToString()
        pluralStringResource(
            id = if (isTvShows) R.plurals.filters_not_a_tv_genre else R.plurals.filters_not_a_movie_genre,
            count = unmatchableGenres.size,
            names
        )
    }
    Column(
        modifier = modifier.padding(horizontal = zedMoviesTheme.spacing.largest),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium, Alignment.CenterVertically)
    ) {
        Image(painter = painterResource(id = R.drawable.no_search_results), contentDescription = null)
        Text(
            text = message,
            style = zedMoviesTheme.typography.medium.h3,
            color = zedMoviesTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        zedMoviesOutlinedButton(textResourceId = R.string.filters_clear, onClick = onClearFilters)
    }
}
