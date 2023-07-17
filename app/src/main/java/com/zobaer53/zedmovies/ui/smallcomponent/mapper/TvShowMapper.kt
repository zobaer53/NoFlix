

package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import com.zobaer53.zedmovies.domain.model.TvShowDetailsModel
import com.zobaer53.zedmovies.domain.model.TvShowModel
import com.zobaer53.zedmovies.data.model.ReleaseDate
import com.zobaer53.zedmovies.data.model.TvShow
import com.zobaer53.zedmovies.data.model.TvShowDetails
import com.zobaer53.zedmovies.ui.smallcomponent.util.roundToOneDecimal

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
