package com.zobaer53.zedmovies.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.data.model.FilterOptions
import com.zobaer53.zedmovies.data.model.FilterTvNetwork
import com.zobaer53.zedmovies.data.model.FilterTvStatus
import com.zobaer53.zedmovies.data.model.FilterTvType
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesCircularProgressIndicator
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesIcon
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import java.text.NumberFormat
import kotlin.math.roundToInt

/**
 * The sheet's collapsible "More filters" section, open when any of its filters is set. While
 * search text is entered, only minimum votes and excluded genres stay enabled: search results
 * don't carry the data the others need.
 */
@Suppress("LongParameterList", "LongMethod")
@Composable
internal fun MoreFilters(
    draft: SearchFilters,
    onDraftChange: (SearchFilters) -> Unit,
    isSearchTextEntered: Boolean,
    options: FilterOptions?,
    isOptionsLoading: Boolean,
    optionsError: Throwable?,
    onRetryOptions: () -> Unit,
    onMoreCountries: () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(draft.hasMoreFilters) }
    val isDiscoverOnlyEnabled = !isSearchTextEntered

    Column(verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = zedMoviesTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.filters_more),
                style = zedMoviesTheme.typography.semiBold.h4,
                color = zedMoviesTheme.colors.accent
            )
            zedMoviesIcon(
                imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = zedMoviesTheme.colors.accent
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)) {
                if (isSearchTextEntered) {
                    Text(
                        text = stringResource(id = R.string.filters_more_search_note),
                        style = zedMoviesTheme.typography.regular.h6,
                        color = zedMoviesTheme.colors.textSecondary
                    )
                }

                FilterSection(titleResourceId = R.string.filters_min_votes) {
                    ChipFlow {
                        VoteSteps.forEach { votes ->
                            FilterOptionChip(
                                label = votes?.let { votesLabel(it) } ?: stringResource(id = R.string.filters_any),
                                selected = draft.minVotes == votes,
                                onClick = { onDraftChange(draft.copy(minVotes = votes)) }
                            )
                        }
                    }
                }

                FilterSection(
                    titleResourceId = R.string.filters_runtime,
                    value = runtimeLabel(draft.runtimeMin, draft.runtimeMax)
                ) {
                    RangeSlider(
                        value = (draft.runtimeMin ?: 0).toFloat()..(draft.runtimeMax ?: MAX_RUNTIME).toFloat(),
                        onValueChange = { range ->
                            val min = (range.start / RUNTIME_STEP).roundToInt() * RUNTIME_STEP
                            val max = (range.endInclusive / RUNTIME_STEP).roundToInt() * RUNTIME_STEP
                            onDraftChange(
                                draft.copy(
                                    runtimeMin = min.takeIf { it > 0 },
                                    runtimeMax = max.takeIf { it < MAX_RUNTIME }
                                )
                            )
                        },
                        valueRange = 0f..MAX_RUNTIME.toFloat(),
                        enabled = isDiscoverOnlyEnabled,
                        colors = sliderColors()
                    )
                }

                FilterSection(titleResourceId = R.string.filters_excluded_genres) {
                    ChipFlow {
                        FilterGenre.entries.forEach { genre ->
                            FilterOptionChip(
                                label = genre.displayName(),
                                selected = genre in draft.excludedGenres,
                                onClick = {
                                    val excluded = if (genre in draft.excludedGenres) {
                                        draft.excludedGenres - genre
                                    } else {
                                        draft.excludedGenres + genre
                                    }
                                    onDraftChange(draft.copy(excludedGenres = excluded, genres = draft.genres - genre))
                                }
                            )
                        }
                    }
                }

                FilterSection(titleResourceId = R.string.filters_origin_country) {
                    ChipFlow {
                        FilterOptionChip(
                            label = stringResource(id = R.string.filters_any_country),
                            selected = draft.originCountry == null,
                            enabled = isDiscoverOnlyEnabled,
                            onClick = { onDraftChange(draft.copy(originCountry = null)) }
                        )
                        val selected = draft.originCountry
                        val shown = if (selected == null || selected in CommonCountries) {
                            CommonCountries
                        } else {
                            CommonCountries + selected
                        }
                        shown.forEach { code ->
                            FilterOptionChip(
                                label = countryName(code),
                                selected = selected == code,
                                enabled = isDiscoverOnlyEnabled,
                                onClick = { onDraftChange(draft.copy(originCountry = code)) }
                            )
                        }
                        FilterOptionChip(
                            label = stringResource(id = R.string.filters_more_languages),
                            selected = false,
                            enabled = isDiscoverOnlyEnabled,
                            onClick = onMoreCountries
                        )
                    }
                }

                FilterSection(
                    titleResourceId = R.string.filters_age_rating,
                    value = options?.let { countryName(it.certificationCountry) },
                    noteResourceId = R.string.filters_movies_only
                ) {
                    FilterOptionsContent(options, isOptionsLoading, optionsError, onRetryOptions) { loaded ->
                        ChipFlow {
                            loaded.certifications.forEach { certification ->
                                FilterOptionChip(
                                    label = certification.code,
                                    selected = certification.code in draft.certifications,
                                    enabled = isDiscoverOnlyEnabled,
                                    onClick = {
                                        val codes = draft.certifications.toggle(certification.code)
                                        onDraftChange(
                                            draft.copy(
                                                certifications = codes,
                                                certificationCountry = loaded.certificationCountry
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                FilterSection(
                    titleResourceId = R.string.filters_streaming,
                    value = options?.let { countryName(it.watchRegion) }
                ) {
                    FilterOptionsContent(options, isOptionsLoading, optionsError, onRetryOptions) { loaded ->
                        var isShowingAll by rememberSaveable { mutableStateOf(false) }
                        val shown = if (isShowingAll) {
                            loaded.watchProviders
                        } else {
                            (loaded.watchProviders.take(PROVIDERS_SHOWN) + draft.watchProviders).distinct()
                        }
                        ChipFlow {
                            shown.forEach { provider ->
                                FilterOptionChip(
                                    label = provider.name,
                                    selected = provider in draft.watchProviders,
                                    enabled = isDiscoverOnlyEnabled,
                                    leadingIcon = provider.logoUrl?.let { url ->
                                        {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(FilterChipDefaults.IconSize)
                                                    .clip(zedMoviesTheme.shapes.extraSmall),
                                                model = url,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    onClick = {
                                        onDraftChange(
                                            draft.copy(
                                                watchProviders = draft.watchProviders.toggle(provider),
                                                watchRegion = loaded.watchRegion
                                            )
                                        )
                                    }
                                )
                            }
                            if (!isShowingAll && loaded.watchProviders.size > PROVIDERS_SHOWN) {
                                FilterOptionChip(
                                    label = stringResource(id = R.string.filters_show_all),
                                    selected = false,
                                    enabled = isDiscoverOnlyEnabled,
                                    onClick = { isShowingAll = true }
                                )
                            }
                        }
                        // TMDB's terms ask apps that show its watch-provider data to credit JustWatch.
                        Text(
                            text = stringResource(id = R.string.filters_streaming_credit),
                            style = zedMoviesTheme.typography.regular.h7,
                            color = zedMoviesTheme.colors.textSecondary
                        )
                    }
                }

                FilterSection(titleResourceId = R.string.filters_tv_network, noteResourceId = R.string.filters_tv_only) {
                    ChipFlow {
                        FilterTvNetwork.entries.forEach { network ->
                            FilterOptionChip(
                                label = network.displayName,
                                selected = network in draft.tvNetworks,
                                enabled = isDiscoverOnlyEnabled,
                                onClick = { onDraftChange(draft.copy(tvNetworks = draft.tvNetworks.toggle(network))) }
                            )
                        }
                    }
                }

                FilterSection(titleResourceId = R.string.filters_tv_status, noteResourceId = R.string.filters_tv_only) {
                    ChipFlow {
                        FilterTvStatus.entries.forEach { status ->
                            FilterOptionChip(
                                label = stringResource(id = status.labelResourceId),
                                selected = status in draft.tvStatuses,
                                enabled = isDiscoverOnlyEnabled,
                                onClick = { onDraftChange(draft.copy(tvStatuses = draft.tvStatuses.toggle(status))) }
                            )
                        }
                    }
                }

                FilterSection(titleResourceId = R.string.filters_tv_type, noteResourceId = R.string.filters_tv_only) {
                    ChipFlow {
                        FilterTvType.entries.forEach { type ->
                            FilterOptionChip(
                                label = stringResource(id = type.labelResourceId),
                                selected = type in draft.tvTypes,
                                enabled = isDiscoverOnlyEnabled,
                                onClick = { onDraftChange(draft.copy(tvTypes = draft.tvTypes.toggle(type))) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** [content] once TMDB's choices are loaded; a spinner, or an error with a retry, before that. */
@Composable
private fun FilterOptionsContent(
    options: FilterOptions?,
    isLoading: Boolean,
    error: Throwable?,
    onRetry: () -> Unit,
    content: @Composable (FilterOptions) -> Unit
) {
    when {
        options != null -> Column(
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small),
            content = { content(options) }
        )
        isLoading || error == null -> zedMoviesCircularProgressIndicator(modifier = Modifier.size(24.dp))
        else -> Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.filters_options_error),
                style = zedMoviesTheme.typography.regular.h5,
                color = zedMoviesTheme.colors.textSecondary
            )
            TextButton(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry), color = zedMoviesTheme.colors.accent)
            }
        }
    }
}

@Composable
internal fun runtimeLabel(min: Int?, max: Int?) = when {
    min != null && max != null -> stringResource(id = R.string.filters_runtime_range, min, max)
    min != null -> stringResource(id = R.string.filters_runtime_from, min)
    max != null -> stringResource(id = R.string.filters_runtime_to, max)
    else -> stringResource(id = R.string.filters_any_runtime)
}

@Composable
private fun votesLabel(votes: Int) = stringResource(id = R.string.filters_votes_value, formatVotes(votes))

internal fun formatVotes(votes: Int): String = NumberFormat.getIntegerInstance().format(votes)

private fun <T> Set<T>.toggle(item: T) = if (item in this) this - item else this + item

private val VoteSteps = listOf(null, 50, 100, 500, 1000)

/** Shown before "More…"; countries that produce much of TMDB's catalogue. */
private val CommonCountries = listOf("US", "GB", "KR", "JP", "IN", "CN", "FR", "ES", "DE", "IT", "TR", "BD")

private const val MAX_RUNTIME = 240
private const val RUNTIME_STEP = 5
private const val PROVIDERS_SHOWN = 20
