package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import com.zobaer53.zedmovies.data.model.Certification
import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.data.model.FilterOptions
import com.zobaer53.zedmovies.data.model.FilterSort
import com.zobaer53.zedmovies.data.model.FilterTvNetwork
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.data.model.WatchProvider
import com.zobaer53.zedmovies.domain.model.FilterGenreModel
import com.zobaer53.zedmovies.domain.model.FilterOptionsModel
import com.zobaer53.zedmovies.domain.model.GenreMatchModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.model.SortModel
import com.zobaer53.zedmovies.domain.model.TvStatusModel
import com.zobaer53.zedmovies.domain.model.TvTypeModel

fun SearchFilters.asSearchFiltersModel() = SearchFiltersModel(
    sort = sort.asSortModel(),
    genres = genres.mapTo(mutableSetOf()) { FilterGenreModel.valueOf(it.name) },
    genreMatch = if (matchAllGenres) GenreMatchModel.ALL else GenreMatchModel.ANY,
    yearFrom = yearFrom,
    yearTo = yearTo,
    minRating = minRating,
    originalLanguage = originalLanguage,
    minVotes = minVotes,
    runtimeMin = runtimeMin,
    runtimeMax = runtimeMax,
    originCountry = originCountry,
    excludedGenres = excludedGenres.mapTo(mutableSetOf()) { FilterGenreModel.valueOf(it.name) },
    certifications = certifications,
    certificationCountry = certificationCountry,
    watchProviders = watchProviders.mapTo(mutableSetOf(), WatchProvider::id),
    watchRegion = watchRegion,
    tvNetworks = tvNetworks.mapTo(mutableSetOf(), FilterTvNetwork::id),
    tvStatuses = tvStatuses.mapTo(mutableSetOf()) { TvStatusModel.valueOf(it.name) },
    tvTypes = tvTypes.mapTo(mutableSetOf()) { TvTypeModel.valueOf(it.name) }
)

fun FilterGenreModel.asFilterGenre() = FilterGenre.valueOf(name)

fun FilterOptionsModel.asFilterOptions() = FilterOptions(
    certificationCountry = certifications.country,
    certifications = certifications.certifications.map { Certification(code = it.code, meaning = it.meaning) },
    watchRegion = watchProviders.region,
    watchProviders = watchProviders.providers.map { WatchProvider(id = it.id, name = it.name, logoUrl = it.logoPath) }
)

private fun FilterSort.asSortModel() = when (this) {
    FilterSort.POPULAR -> SortModel.POPULARITY_DESC
    FilterSort.TOP_RATED -> SortModel.RATING_DESC
    FilterSort.NEWEST -> SortModel.RELEASE_DATE_DESC
    FilterSort.OLDEST -> SortModel.RELEASE_DATE_ASC
    FilterSort.TITLE -> SortModel.TITLE_ASC
}
