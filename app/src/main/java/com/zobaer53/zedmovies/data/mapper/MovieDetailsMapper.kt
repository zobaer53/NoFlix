

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovieDetails
import com.zobaer53.zedmovies.data.database.model.movie.MovieDetailsEntity
import com.zobaer53.zedmovies.domain.model.MovieDetailsModel

fun NetworkMovieDetails.asMovieDetailsEntity() = MovieDetailsEntity(
    id = id,
    adult = adult,
    backdropPath = backdropPath?.asImageUrl(),
    budget = budget,
    genres = genres.asGenres(),
    homepage = homepage,
    imdbId = imdbId,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath?.asImageUrl(),
    releaseDate = releaseDate,
    revenue = revenue,
    runtime = runtime,
    status = status,
    tagline = tagline,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits.asCredits()
)

fun MovieDetailsEntity.asMovieDetailsModel(isWishlisted: Boolean) = MovieDetailsModel(
    id = id,
    adult = adult,
    backdropPath = backdropPath,
    budget = budget,
    genres = genres.asGenreModels(),
    homepage = homepage,
    imdbId = imdbId,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    releaseDate = releaseDate,
    revenue = revenue,
    runtime = runtime,
    status = status,
    tagline = tagline,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount,
    credits = credits.asCreditsModel(),
    isWishlisted = isWishlisted
)
