package com.zobaer53.zedmovies.data.model

import androidx.annotation.StringRes
import com.zobaer53.zedmovies.R
import kotlinx.serialization.Serializable

/** The Search tab's filters (UI copy of `SearchFiltersModel`); serializable for the saved state. */
@Serializable
data class SearchFilters(
    val sort: FilterSort = FilterSort.POPULAR,
    val genres: Set<FilterGenre> = emptySet(),
    val matchAllGenres: Boolean = false,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val minRating: Double? = null,
    val originalLanguage: String? = null
) {
    val isActive: Boolean get() = this != SearchFilters()

    /** One entry per removable chip, in display order. */
    val activeFilters: List<ActiveFilter>
        get() = buildList {
            if (sort != FilterSort.POPULAR) add(ActiveFilter.Sort(sort))
            genres.sorted().forEach { add(ActiveFilter.Genre(it)) }
            if (yearFrom != null || yearTo != null) add(ActiveFilter.Years(yearFrom, yearTo))
            minRating?.let { add(ActiveFilter.Rating(it)) }
            originalLanguage?.let { add(ActiveFilter.Language(it)) }
        }

    fun without(filter: ActiveFilter) = when (filter) {
        is ActiveFilter.Sort -> copy(sort = FilterSort.POPULAR)
        is ActiveFilter.Genre -> copy(genres = genres - filter.genre)
        is ActiveFilter.Years -> copy(yearFrom = null, yearTo = null)
        is ActiveFilter.Rating -> copy(minRating = null)
        is ActiveFilter.Language -> copy(originalLanguage = null)
    }.let { if (it.genres.size < 2) it.copy(matchAllGenres = false) else it }
}

sealed interface ActiveFilter {
    data class Sort(val sort: FilterSort) : ActiveFilter
    data class Genre(val genre: FilterGenre) : ActiveFilter
    data class Years(val from: Int?, val to: Int?) : ActiveFilter
    data class Rating(val minRating: Double) : ActiveFilter
    data class Language(val code: String) : ActiveFilter
}

enum class FilterSort(@StringRes val labelResourceId: Int) {
    POPULAR(R.string.filters_sort_popular),
    TOP_RATED(R.string.filters_sort_top_rated),
    NEWEST(R.string.filters_sort_newest),
    OLDEST(R.string.filters_sort_oldest),
    TITLE(R.string.filters_sort_title)
}

/** Same constants as `FilterGenreModel`. */
enum class FilterGenre(@StringRes val nameResourceId: Int) {
    ACTION(R.string.action),
    ADVENTURE(R.string.adventure),
    ANIMATION(R.string.animation),
    COMEDY(R.string.comedy),
    CRIME(R.string.crime),
    DOCUMENTARY(R.string.documentary),
    DRAMA(R.string.drama),
    FAMILY(R.string.family),
    FANTASY(R.string.fantasy),
    HISTORY(R.string.history),
    HORROR(R.string.horror),
    KIDS(R.string.kids),
    MUSIC(R.string.music),
    MYSTERY(R.string.mystery),
    NEWS(R.string.news),
    REALITY(R.string.reality),
    ROMANCE(R.string.romance),
    SCIENCE_FICTION(R.string.science_fiction),
    SOAP(R.string.soap),
    TALK(R.string.talk),
    THRILLER(R.string.thriller),
    TV_MOVIE(R.string.tv_movie),
    WAR(R.string.war),
    WESTERN(R.string.western)
}
