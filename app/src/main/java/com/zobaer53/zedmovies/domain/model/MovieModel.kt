

package com.zobaer53.zedmovies.domain.model

import kotlinx.datetime.LocalDate

data class MovieModel(
    val id: Int,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: LocalDate?,
    val adult: Boolean,
    val genres: List<GenreModel>,
    val originalTitle: String,
    val originalLanguage: String,
    val voteAverage: Double,
    val voteCount: Int,
    val posterPath: String?,
    val backdropPath: String?,
    val video: Boolean
)
