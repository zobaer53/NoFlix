

package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import com.zobaer53.zedmovies.core.domain.model.MovieDetailsModel
import com.zobaer53.zedmovies.core.domain.model.MovieModel
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.MovieDetails
import com.zobaer53.zedmovies.data.model.ReleaseDate
import com.zobaer53.zedmovies.ui.smallcomponent.util.roundToOneDecimal

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
