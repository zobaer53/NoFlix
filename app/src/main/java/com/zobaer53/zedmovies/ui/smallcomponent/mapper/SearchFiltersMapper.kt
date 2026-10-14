package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.data.model.FilterSort
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.domain.model.FilterGenreModel
import com.zobaer53.zedmovies.domain.model.GenreMatchModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.model.SortModel

fun SearchFilters.asSearchFiltersModel() = SearchFiltersModel(
    sort = sort.asSortModel(),
    genres = genres.mapTo(mutableSetOf()) { FilterGenreModel.valueOf(it.name) },
    genreMatch = if (matchAllGenres) GenreMatchModel.ALL else GenreMatchModel.ANY,
    yearFrom = yearFrom,
    yearTo = yearTo,
    minRating = minRating,
    originalLanguage = originalLanguage
)

fun FilterGenreModel.asFilterGenre() = FilterGenre.valueOf(name)

private fun FilterSort.asSortModel() = when (this) {
    FilterSort.POPULAR -> SortModel.POPULARITY_DESC
    FilterSort.TOP_RATED -> SortModel.RATING_DESC
    FilterSort.NEWEST -> SortModel.RELEASE_DATE_DESC
    FilterSort.OLDEST -> SortModel.RELEASE_DATE_ASC
    FilterSort.TITLE -> SortModel.TITLE_ASC
}
