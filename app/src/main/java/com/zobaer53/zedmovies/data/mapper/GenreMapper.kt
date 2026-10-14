

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.database.model.common.Genre
import com.zobaer53.zedmovies.data.network.model.common.NetworkGenre
import com.zobaer53.zedmovies.data.network.model.common.NetworkGenreWithId

// Unknown genre ids are skipped, so a genre TMDB adds later doesn't crash the lists.
internal fun List<NetworkGenre>.asGenres() = mapNotNull { it.id.asGenre() }

@JvmName("intListAsGenres")
internal fun List<Int>.asGenres() = mapNotNull(Int::asGenre)

@JvmName("intListAsGenreModels")
internal fun List<Int>.asGenreModels() = mapNotNull(Int::asGenreModel)

internal fun Genre.asGenreModel() = com.zobaer53.zedmovies.domain.model.GenreModel[genreName]
internal fun List<Genre>.asGenreModels() = map(Genre::asGenreModel)

private fun Int.asGenre() = NetworkGenreWithId[this]?.let { Genre[it.genreName] }
private fun Int.asGenreModel() =
    NetworkGenreWithId[this]?.let { com.zobaer53.zedmovies.domain.model.GenreModel[it.genreName] }
