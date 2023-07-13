

package com.zobaer53.zedmovies.core.ui.mapper

import com.zobaer53.zedmovies.core.domain.model.TvShowDetailsModel
import com.zobaer53.zedmovies.core.domain.model.TvShowModel
import com.zobaer53.zedmovies.core.model.ReleaseDate
import com.zobaer53.zedmovies.core.model.TvShow
import com.zobaer53.zedmovies.core.model.TvShowDetails
import com.zobaer53.zedmovies.core.ui.util.roundToOneDecimal

fun TvShowModel.asTvShow() = TvShow(
    id = id,
    name = name,
    overview = overview,
    firstAirDate = firstAirDate?.asReleaseDate() ?: ReleaseDate(),
    genres = genres.asGenres(),
    voteAverage = voteAverage.roundToOneDecimal(),
    posterPath = posterPath,
    backdropPath = backdropPath
)

fun TvShowDetailsModel.asTvShowDetails() = TvShowDetails(
    id = id,
    name = name,
    backdropPath = backdropPath,
    episodeRunTime = episodeRunTime.maxOrNull() ?: NoTvShowRuntimeValue,
    firstAirDate = firstAirDate?.asReleaseDate() ?: ReleaseDate(),
    genres = genres.asGenres(),
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    voteAverage = voteAverage.roundToOneDecimal(),
    credits = credits.asCredits(),
    isWishlisted = isWishlisted
)

const val NoTvShowRuntimeValue = 0
