package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.util.Constants.Discover
import com.zobaer53.zedmovies.domain.model.FilterMediaTypeModel
import com.zobaer53.zedmovies.domain.model.GenreMatchModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.model.SortModel
import java.time.LocalDate

internal fun SearchFiltersModel.asDiscoverMovieQuery(today: LocalDate = LocalDate.now()) =
    asDiscoverQuery(FilterMediaTypeModel.MOVIE, today)

internal fun SearchFiltersModel.asDiscoverTvQuery(today: LocalDate = LocalDate.now()) =
    asDiscoverQuery(FilterMediaTypeModel.TV_SHOW, today)

/** Genres the media type doesn't have are left out (see [SearchFiltersModel.unmatchableGenres]). */
private fun SearchFiltersModel.asDiscoverQuery(
    mediaType: FilterMediaTypeModel,
    today: LocalDate
): Map<String, String> = buildMap {
    val isMovie = mediaType == FilterMediaTypeModel.MOVIE
    put(Discover.INCLUDE_ADULT, false.toString())
    put(Discover.SORT_BY, sort.asSortBy(isMovie))

    val genreIds = genres.mapNotNull { it.idFor(mediaType) }.distinct()
    if (genreIds.isNotEmpty()) {
        val separator = if (genreMatch == GenreMatchModel.ALL) GENRES_ALL else GENRES_ANY
        put(Discover.WITH_GENRES, genreIds.joinToString(separator))
    }

    val dateFrom = if (isMovie) Discover.PRIMARY_RELEASE_DATE_GTE else Discover.FIRST_AIR_DATE_GTE
    val dateTo = if (isMovie) Discover.PRIMARY_RELEASE_DATE_LTE else Discover.FIRST_AIR_DATE_LTE
    yearFrom?.let { put(dateFrom, LocalDate.of(it, 1, 1).toString()) }
    // Newest first would otherwise start with announced titles that are years away.
    val upperBound = yearTo?.let { LocalDate.of(it, 12, 31) }
        ?: today.takeIf { sort == SortModel.RELEASE_DATE_DESC }
    upperBound?.let { put(dateTo, it.toString()) }

    minRating?.let { put(Discover.VOTE_AVERAGE_GTE, it.toString()) }
    // Without a vote floor, titles with a single 10/10 vote rank first.
    val minVotes = when {
        sort == SortModel.RATING_DESC -> if (isMovie) RATING_SORT_MOVIE_VOTES else RATING_SORT_TV_VOTES
        minRating != null -> if (isMovie) MIN_RATING_MOVIE_VOTES else MIN_RATING_TV_VOTES
        else -> null
    }
    minVotes?.let { put(Discover.VOTE_COUNT_GTE, it.toString()) }

    originalLanguage?.let { put(Discover.WITH_ORIGINAL_LANGUAGE, it) }
}

private fun SortModel.asSortBy(isMovie: Boolean) = when (this) {
    SortModel.POPULARITY_DESC -> "popularity.desc"
    SortModel.RATING_DESC -> "vote_average.desc"
    SortModel.RELEASE_DATE_DESC -> if (isMovie) "primary_release_date.desc" else "first_air_date.desc"
    SortModel.RELEASE_DATE_ASC -> if (isMovie) "primary_release_date.asc" else "first_air_date.asc"
    SortModel.TITLE_ASC -> if (isMovie) "title.asc" else "name.asc"
}

private const val GENRES_ANY = "|"
private const val GENRES_ALL = ","

private const val RATING_SORT_MOVIE_VOTES = 200
private const val RATING_SORT_TV_VOTES = 100
private const val MIN_RATING_MOVIE_VOTES = 50
private const val MIN_RATING_TV_VOTES = 20
