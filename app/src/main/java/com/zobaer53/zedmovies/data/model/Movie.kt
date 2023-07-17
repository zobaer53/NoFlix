

package com.zobaer53.zedmovies.data.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: ReleaseDate,
    val genres: List<Genre>,
    val voteAverage: Double,
    val posterPath: String?,
    val backdropPath: String?
)
