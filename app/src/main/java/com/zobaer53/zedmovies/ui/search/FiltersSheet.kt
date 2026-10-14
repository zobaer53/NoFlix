package com.zobaer53.zedmovies.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.data.model.FilterSort
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.Year
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Edits a copy of [filters] and hands it to [onApply] on "Show results", so dragging a slider
 * doesn't start a request per step. [isSortEnabled] is false while search text is entered: search
 * results keep TMDB's relevance order.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun FiltersSheet(
    filters: SearchFilters,
    isSortEnabled: Boolean,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var draft by rememberSaveable(stateSaver = SearchFiltersSaver) { mutableStateOf(filters) }
    var isLanguageListOpen by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = zedMoviesTheme.colors.primary,
        contentColor = zedMoviesTheme.colors.textPrimary
    ) {
        // Exposes the sliders' test tags to Maestro as resource ids.
        Column(modifier = Modifier.semantics { testTagsAsResourceId = true }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = zedMoviesTheme.spacing.extraMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.filters),
                    style = zedMoviesTheme.typography.semiBold.h3
                )
                TextButton(onClick = { draft = SearchFilters() }, enabled = draft.isActive) {
                    Text(
                        text = stringResource(id = R.string.filters_reset),
                        style = zedMoviesTheme.typography.medium.h5,
                        color = if (draft.isActive) {
                            zedMoviesTheme.colors.accent
                        } else {
                            zedMoviesTheme.colors.textSecondary
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = zedMoviesTheme.spacing.extraMedium),
                verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)
            ) {
                FilterSection(titleResourceId = R.string.filters_sort) {
                    if (!isSortEnabled) {
                        Text(
                            text = stringResource(id = R.string.filters_sort_disabled),
                            style = zedMoviesTheme.typography.regular.h6,
                            color = zedMoviesTheme.colors.textSecondary
                        )
                    }
                    ChipFlow {
                        FilterSort.entries.forEach { sort ->
                            FilterOptionChip(
                                label = stringResource(id = sort.labelResourceId),
                                selected = draft.sort == sort,
                                enabled = isSortEnabled,
                                onClick = { draft = draft.copy(sort = sort) }
                            )
                        }
                    }
                }

                FilterSection(titleResourceId = R.string.filters_genres) {
                    ChipFlow {
                        FilterGenre.entries.forEach { genre ->
                            FilterOptionChip(
                                label = genre.displayName(),
                                selected = genre in draft.genres,
                                onClick = {
                                    val genres = if (genre in draft.genres) {
                                        draft.genres - genre
                                    } else {
                                        draft.genres + genre
                                    }
                                    draft = draft.copy(
                                        genres = genres,
                                        matchAllGenres = draft.matchAllGenres && genres.size > 1
                                    )
                                }
                            )
                        }
                    }
                    AnimatedVisibility(visible = draft.genres.size > 1) {
                        ChipFlow {
                            FilterOptionChip(
                                label = stringResource(id = R.string.filters_genres_any),
                                selected = !draft.matchAllGenres,
                                onClick = { draft = draft.copy(matchAllGenres = false) }
                            )
                            FilterOptionChip(
                                label = stringResource(id = R.string.filters_genres_all),
                                selected = draft.matchAllGenres,
                                onClick = { draft = draft.copy(matchAllGenres = true) }
                            )
                        }
                    }
                }

                val maxYear = remember { Year.now().value + 1 }
                FilterSection(
                    titleResourceId = R.string.filters_release_year,
                    value = yearsLabel(draft.yearFrom, draft.yearTo)
                ) {
                    RangeSlider(
                        modifier = Modifier.testTag(YEAR_SLIDER_TAG),
                        value = (draft.yearFrom ?: MIN_YEAR).toFloat()..(draft.yearTo ?: maxYear).toFloat(),
                        onValueChange = { range ->
                            draft = draft.copy(
                                yearFrom = range.start.roundToInt().takeIf { it > MIN_YEAR },
                                yearTo = range.endInclusive.roundToInt().takeIf { it < maxYear }
                            )
                        },
                        valueRange = MIN_YEAR.toFloat()..maxYear.toFloat(),
                        colors = sliderColors()
                    )
                }

                FilterSection(
                    titleResourceId = R.string.filters_min_rating,
                    value = draft.minRating?.let {
                        stringResource(id = R.string.filters_rating_value, formatRating(it))
                    } ?: stringResource(id = R.string.filters_any_rating)
                ) {
                    Slider(
                        modifier = Modifier.testTag(RATING_SLIDER_TAG),
                        value = (draft.minRating ?: 0.0).toFloat(),
                        onValueChange = { value ->
                            val rating = (value * 2).roundToInt() / 2.0
                            draft = draft.copy(minRating = rating.takeIf { it > 0 })
                        },
                        valueRange = 0f..MAX_RATING,
                        steps = RATING_STEPS,
                        colors = sliderColors()
                    )
                }

                FilterSection(titleResourceId = R.string.filters_original_language) {
                    ChipFlow {
                        FilterOptionChip(
                            label = stringResource(id = R.string.filters_any_language),
                            selected = draft.originalLanguage == null,
                            onClick = { draft = draft.copy(originalLanguage = null) }
                        )
                        val selected = draft.originalLanguage
                        val shown = if (selected == null || selected in CommonLanguages) {
                            CommonLanguages
                        } else {
                            CommonLanguages + selected
                        }
                        shown.forEach { code ->
                            FilterOptionChip(
                                label = languageName(code),
                                selected = selected == code,
                                onClick = { draft = draft.copy(originalLanguage = code) }
                            )
                        }
                        FilterOptionChip(
                            label = stringResource(id = R.string.filters_more_languages),
                            selected = false,
                            onClick = { isLanguageListOpen = true }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(zedMoviesTheme.spacing.extraSmall))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = zedMoviesTheme.spacing.extraMedium,
                        vertical = zedMoviesTheme.spacing.medium
                    ),
                onClick = {
                    onApply(draft)
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                shape = zedMoviesTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = zedMoviesTheme.colors.accent,
                    contentColor = zedMoviesTheme.colors.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.filters_show_results),
                    style = zedMoviesTheme.typography.semiBold.h4
                )
            }
        }
    }

    if (isLanguageListOpen) {
        LanguageListDialog(
            onSelect = { code ->
                draft = draft.copy(originalLanguage = code)
                isLanguageListOpen = false
            },
            onDismiss = { isLanguageListOpen = false }
        )
    }
}

@Composable
private fun FilterSection(
    @StringRes titleResourceId: Int,
    value: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = titleResourceId),
                style = zedMoviesTheme.typography.semiBold.h4,
                color = zedMoviesTheme.colors.textPrimary
            )
            value?.let {
                Text(
                    text = it,
                    style = zedMoviesTheme.typography.medium.h5,
                    color = zedMoviesTheme.colors.accent
                )
            }
        }
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.small)) {
        content()
    }
}

@Composable
private fun FilterOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = { Text(text = label, style = zedMoviesTheme.typography.medium.h5) },
        shape = zedMoviesTheme.shapes.small,
        colors = filterChipColors(),
        border = null
    )
}

@Composable
internal fun filterChipColors(): SelectableChipColors = FilterChipDefaults.filterChipColors(
    containerColor = zedMoviesTheme.colors.primaryVariant,
    labelColor = zedMoviesTheme.colors.textPrimary,
    iconColor = zedMoviesTheme.colors.textPrimary,
    selectedContainerColor = zedMoviesTheme.colors.accent,
    selectedLabelColor = zedMoviesTheme.colors.primary,
    selectedLeadingIconColor = zedMoviesTheme.colors.primary,
    selectedTrailingIconColor = zedMoviesTheme.colors.primary,
    disabledContainerColor = zedMoviesTheme.colors.primaryVariant,
    disabledLabelColor = zedMoviesTheme.colors.textSecondary,
    disabledSelectedContainerColor = zedMoviesTheme.colors.textSecondary
)

@Composable
private fun sliderColors() = SliderDefaults.colors(
    thumbColor = zedMoviesTheme.colors.accent,
    activeTrackColor = zedMoviesTheme.colors.accent,
    activeTickColor = zedMoviesTheme.colors.primary,
    inactiveTrackColor = zedMoviesTheme.colors.primaryVariant,
    inactiveTickColor = zedMoviesTheme.colors.textSecondary
)

/** Every ISO 639-1 language, named in the device language. */
@Composable
private fun LanguageListDialog(onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val languages = remember {
        Locale.getISOLanguages()
            .map { code -> code to displayLanguage(code) }
            .filter { (code, name) -> name.isNotBlank() && name != code }
            .sortedBy { (_, name) -> name }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel), color = zedMoviesTheme.colors.accent)
            }
        },
        title = { Text(text = stringResource(id = R.string.filters_original_language)) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(languages, key = { it.first }) { (code, name) ->
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = { onSelect(code) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = name,
                            style = zedMoviesTheme.typography.medium.h4,
                            color = zedMoviesTheme.colors.textPrimary
                        )
                    }
                }
            }
        },
        containerColor = zedMoviesTheme.colors.primary,
        titleContentColor = zedMoviesTheme.colors.textPrimary
    )
}

@Composable
internal fun FilterGenre.displayName() =
    stringResource(id = nameResourceId).replaceFirstChar { it.titlecase(Locale.getDefault()) }

@Composable
internal fun yearsLabel(from: Int?, to: Int?) = when {
    from != null && to != null -> stringResource(id = R.string.filters_year_range, from, to)
    from != null -> stringResource(id = R.string.filters_year_from, from)
    to != null -> stringResource(id = R.string.filters_year_to, to)
    else -> stringResource(id = R.string.filters_any_year)
}

internal fun languageName(code: String) = displayLanguage(code)

internal fun formatRating(rating: Double) = String.format(Locale.getDefault(), "%.1f", rating)

private fun displayLanguage(code: String) = Locale.forLanguageTag(code)
    .getDisplayLanguage(Locale.getDefault())
    .replaceFirstChar { it.titlecase(Locale.getDefault()) }

private val SearchFiltersSaver = Saver<SearchFilters, String>(
    save = { Json.encodeToString(SearchFilters.serializer(), it) },
    restore = { Json.decodeFromString(SearchFilters.serializer(), it) }
)

/** Shown before "More…"; the most common original languages of titles on TMDB. */
private val CommonLanguages = listOf("en", "ko", "ja", "zh", "hi", "bn", "es", "fr", "de", "it", "tr")

private const val YEAR_SLIDER_TAG = "filters_year_slider"
private const val RATING_SLIDER_TAG = "filters_rating_slider"

private const val MIN_YEAR = 1900
private const val MAX_RATING = 10f
private const val RATING_STEPS = 19
