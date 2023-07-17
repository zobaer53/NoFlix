

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.core.network.model.tvshow.NetworkTvShowDetails
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowDetailsEntity
import com.zobaer53.zedmovies.core.domain.model.TvShowDetailsModel

fun NetworkTvShowDetails.asTvShowDetailsEntity() = TvShowDetailsEntity(
    id = id,
    name = name,
    adult = adult,
    backdropPath = backdropPath?.asImageUrl(),
    episodeRunTime = episodeRunTime,
    firstAirDate = firstAirDate,
    genres = genres.asGenres(),
    homepage = homepage,
    inProduction = inProduction,
    languages = languages,
    lastAirDate = lastAirDate,
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    originCountry = originCountry,
    originalLanguage = originalLanguage,
    originalName = originalName,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath?.asImageUrl(),
    status = status,
    tagline = tagline,
    type = type,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits.asCredits()
)

fun TvShowDetailsEntity.asTvShowDetailsModel(isWishlisted: Boolean) = TvShowDetailsModel(
    id = id,
    name = name,
    adult = adult,
    backdropPath = backdropPath,
    episodeRunTime = episodeRunTime,
    firstAirDate = firstAirDate,
    genres = genres.asGenreModels(),
    homepage = homepage,
    inProduction = inProduction,
    languages = languages,
    lastAirDate = lastAirDate,
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    originCountry = originCountry,
    originalLanguage = originalLanguage,
    originalName = originalName,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    status = status,
    tagline = tagline,
    type = type,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits.asCreditsModel(),
    isWishlisted = isWishlisted
)
