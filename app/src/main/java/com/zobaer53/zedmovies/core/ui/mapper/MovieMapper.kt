

package com.zobaer53.zedmovies.core.ui.mapper

import com.zobaer53.zedmovies.core.domain.model.MovieDetailsModel
import com.zobaer53.zedmovies.core.domain.model.MovieModel
import com.zobaer53.zedmovies.core.model.Movie
import com.zobaer53.zedmovies.core.model.MovieDetails
import com.zobaer53.zedmovies.core.model.ReleaseDate
import com.zobaer53.zedmovies.core.ui.util.roundToOneDecimal

fun MovieModel.asMovie() = Movie(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate?.asReleaseDate() ?: ReleaseDate(),
    genres = genres.asGenres(),
    voteAverage = voteAverage.roundToOneDecimal(),
    posterPath = posterPath,
    backdropPath = backdropPath
)

fun MovieDetailsModel.asMovieDetails() = MovieDetails(
    id = id,
    title = title,
    overview = overview,
    backdropPath = backdropPath,
    budget = budget,
    genres = genres.asGenres(),
    posterPath = posterPath,
    releaseDate = releaseDate?.asReleaseDate() ?: ReleaseDate(),
    runtime = runtime ?: NoMovieRuntimeValue,
    video = video,
    voteAverage = voteAverage.roundToOneDecimal(),
    voteCount = voteCount,
    credits = credits.asCredits(),
    isWishlisted = isWishlisted
)

const val NoMovieRuntimeValue = 0
