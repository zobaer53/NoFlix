
package com.zobaer53.zedmovies.core.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.core.domain.model.GenreModel
import com.zobaer53.zedmovies.core.model.Genre
import com.zobaer53.zedmovies.R


@Composable
fun List<Genre>.asNames() = map { genre -> stringResource(id = genre.nameResourceId) }

internal fun List<GenreModel>.asGenres() = map { genre ->
    Genre(nameResourceId = genre.asNameResourceId())
}

private fun GenreModel.asNameResourceId() = genresNameResources.getValue(this)

private val genresNameResources = mapOf(
    GenreModel.ACTION to R.string.action,
    GenreModel.ADVENTURE to R.string.adventure,
    GenreModel.ACTION_ADVENTURE to R.string.action_adventure,
    GenreModel.ANIMATION to R.string.animation,
    GenreModel.COMEDY to R.string.comedy,
    GenreModel.CRIME to R.string.crime,
    GenreModel.DOCUMENTARY to R.string.documentary,
    GenreModel.DRAMA to R.string.drama,
    GenreModel.FAMILY to R.string.family,
    GenreModel.FANTASY to R.string.fantasy,
    GenreModel.HISTORY to R.string.history,
    GenreModel.HORROR to R.string.horror,
    GenreModel.KIDS to R.string.kids,
    GenreModel.MUSIC to R.string.music,
    GenreModel.MYSTERY to R.string.mystery,
    GenreModel.NEWS to R.string.news,
    GenreModel.REALITY to R.string.reality,
    GenreModel.ROMANCE to R.string.romance,
    GenreModel.SCIENCE_FICTION to R.string.science_fiction,
    GenreModel.SCIENCE_FICTION_FANTASY to R.string.science_fiction_fantasy,
    GenreModel.SOAP to R.string.soap,
    GenreModel.TALK to R.string.talk,
    GenreModel.THRILLER to R.string.thriller,
    GenreModel.TV_MOVIE to R.string.tv_movie,
    GenreModel.WAR to R.string.war,
    GenreModel.WAR_POLITICS to R.string.war_politics,
    GenreModel.WESTERN to R.string.western
)
