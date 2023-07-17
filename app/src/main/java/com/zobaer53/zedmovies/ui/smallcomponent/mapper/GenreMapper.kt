
package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.domain.model.GenreModel
import com.zobaer53.zedmovies.data.model.Genre
import com.zobaer53.zedmovies.R


@Composable
fun List<Genre>.asNames() = map { genre -> stringResource(id = genre.nameResourceId) }

internal fun List<com.zobaer53.zedmovies.domain.model.GenreModel>.asGenres() = map { genre ->
    Genre(nameResourceId = genre.asNameResourceId())
}

private fun com.zobaer53.zedmovies.domain.model.GenreModel.asNameResourceId() = genresNameResources.getValue(this)

private val genresNameResources = mapOf(
    com.zobaer53.zedmovies.domain.model.GenreModel.ACTION to R.string.action,
    com.zobaer53.zedmovies.domain.model.GenreModel.ADVENTURE to R.string.adventure,
    com.zobaer53.zedmovies.domain.model.GenreModel.ACTION_ADVENTURE to R.string.action_adventure,
    com.zobaer53.zedmovies.domain.model.GenreModel.ANIMATION to R.string.animation,
    com.zobaer53.zedmovies.domain.model.GenreModel.COMEDY to R.string.comedy,
    com.zobaer53.zedmovies.domain.model.GenreModel.CRIME to R.string.crime,
    com.zobaer53.zedmovies.domain.model.GenreModel.DOCUMENTARY to R.string.documentary,
    com.zobaer53.zedmovies.domain.model.GenreModel.DRAMA to R.string.drama,
    com.zobaer53.zedmovies.domain.model.GenreModel.FAMILY to R.string.family,
    com.zobaer53.zedmovies.domain.model.GenreModel.FANTASY to R.string.fantasy,
    com.zobaer53.zedmovies.domain.model.GenreModel.HISTORY to R.string.history,
    com.zobaer53.zedmovies.domain.model.GenreModel.HORROR to R.string.horror,
    com.zobaer53.zedmovies.domain.model.GenreModel.KIDS to R.string.kids,
    com.zobaer53.zedmovies.domain.model.GenreModel.MUSIC to R.string.music,
    com.zobaer53.zedmovies.domain.model.GenreModel.MYSTERY to R.string.mystery,
    com.zobaer53.zedmovies.domain.model.GenreModel.NEWS to R.string.news,
    com.zobaer53.zedmovies.domain.model.GenreModel.REALITY to R.string.reality,
    com.zobaer53.zedmovies.domain.model.GenreModel.ROMANCE to R.string.romance,
    com.zobaer53.zedmovies.domain.model.GenreModel.SCIENCE_FICTION to R.string.science_fiction,
    com.zobaer53.zedmovies.domain.model.GenreModel.SCIENCE_FICTION_FANTASY to R.string.science_fiction_fantasy,
    com.zobaer53.zedmovies.domain.model.GenreModel.SOAP to R.string.soap,
    com.zobaer53.zedmovies.domain.model.GenreModel.TALK to R.string.talk,
    com.zobaer53.zedmovies.domain.model.GenreModel.THRILLER to R.string.thriller,
    com.zobaer53.zedmovies.domain.model.GenreModel.TV_MOVIE to R.string.tv_movie,
    com.zobaer53.zedmovies.domain.model.GenreModel.WAR to R.string.war,
    com.zobaer53.zedmovies.domain.model.GenreModel.WAR_POLITICS to R.string.war_politics,
    com.zobaer53.zedmovies.domain.model.GenreModel.WESTERN to R.string.western
)
