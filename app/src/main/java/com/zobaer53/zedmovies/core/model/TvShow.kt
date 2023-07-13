
package com.zobaer53.zedmovies.core.model

data class TvShow(
    val id: Int,
    val name: String,
    val overview: String,
    val firstAirDate: ReleaseDate,
    val genres: List<Genre>,
    val voteAverage: Double,
    val posterPath: String?,
    val backdropPath: String?
)
