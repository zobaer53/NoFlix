
package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.tvshow.NetworkTvShow
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.domain.model.TvShowModel

internal fun NetworkTvShow.asTvShowEntity(mediaType: MediaType.TvShow) = TvShowEntity(
    mediaType = mediaType,
    networkId = id,
    name = name,
    overview = overview,
    popularity = popularity,
    firstAirDate = firstAirDate,
    genres = genreIds.asGenres(),
    originalName = originalName,
    originalLanguage = originalLanguage,
    originCountry = originCountry,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath?.asImageUrl(),
    backdropPath = backdropPath?.asImageUrl()
)

internal fun TvShowEntity.asTvShowModel() = TvShowModel(
    id = networkId,
    name = name,
    overview = overview,
    popularity = popularity,
    firstAirDate = firstAirDate,
    genres = genres.asGenreModels(),
    originalName = originalName,
    originalLanguage = originalLanguage,
    originCountry = originCountry,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath,
    backdropPath = backdropPath
)

internal fun NetworkTvShow.asTvShowModel() = TvShowModel(
    id = id,
    name = name,
    overview = overview,
    popularity = popularity,
    firstAirDate = firstAirDate,
    genres = genreIds.asGenreModels(),
    originalName = originalName,
    originalLanguage = originalLanguage,
    originCountry = originCountry,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath?.asImageUrl(),
    backdropPath = backdropPath?.asImageUrl()
)
