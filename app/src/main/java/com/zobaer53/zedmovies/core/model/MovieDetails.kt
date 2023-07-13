

package com.zobaer53.zedmovies.core.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val backdropPath: String?,
    val budget: Int,
    val genres: List<Genre>,
    val posterPath: String?,
    val releaseDate: ReleaseDate,
    val runtime: Int,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
    val credits: Credits,
    val isWishlisted: Boolean
)
