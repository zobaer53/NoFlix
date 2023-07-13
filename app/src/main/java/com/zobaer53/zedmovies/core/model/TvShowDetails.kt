

package com.zobaer53.zedmovies.core.model

data class TvShowDetails(
    val id: Int,
    val name: String,
    val backdropPath: String?,
    val episodeRunTime: Int,
    val firstAirDate: ReleaseDate,
    val genres: List<Genre>,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val overview: String,
    val popularity: Double,
    val posterPath: String?,
    val voteAverage: Double,
    val credits: Credits,
    val isWishlisted: Boolean
)
