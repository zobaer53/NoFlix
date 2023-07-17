

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovie
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.domain.model.MovieModel

internal fun NetworkMovie.asMovieEntity(mediaType: MediaType.Movie) = MovieEntity(
    mediaType = mediaType,
    networkId = id,
    title = title,
    overview = overview,
    popularity = popularity,
    releaseDate = releaseDate,
    adult = adult,
    genres = genreIds.asGenres(),
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath?.asImageUrl(),
    backdropPath = backdropPath?.asImageUrl(),
    video = video
)

internal fun MovieEntity.asMovieModel() = MovieModel(
    id = networkId,
    title = title,
    overview = overview,
    popularity = popularity,
    releaseDate = releaseDate,
    adult = adult,
    genres = genres.asGenreModels(),
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath,
    backdropPath = backdropPath,
    video = video
)

internal fun NetworkMovie.asMovieModel() = MovieModel(
    id = id,
    title = title,
    overview = overview,
    popularity = popularity,
    releaseDate = releaseDate,
    adult = adult,
    genres = genreIds.asGenreModels(),
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    voteAverage = voteAverage,
    voteCount = voteCount,
    posterPath = posterPath?.asImageUrl(),
    backdropPath = backdropPath?.asImageUrl(),
    video = video
)
