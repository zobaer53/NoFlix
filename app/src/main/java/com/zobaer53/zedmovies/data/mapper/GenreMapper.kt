

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.database.model.common.Genre
import com.zobaer53.zedmovies.data.network.model.common.NetworkGenre
import com.zobaer53.zedmovies.data.network.model.common.NetworkGenreWithId

internal fun NetworkGenre.asGenre() = id.asGenre()
internal fun List<NetworkGenre>.asGenres() = map(NetworkGenre::asGenre)

@JvmName("intListAsGenres")
internal fun List<Int>.asGenres() = map(Int::asGenre)

@JvmName("intListAsGenreModels")
internal fun List<Int>.asGenreModels() = map(Int::asGenreModel)

internal fun Genre.asGenreModel() = com.zobaer53.zedmovies.domain.model.GenreModel[genreName]
internal fun List<Genre>.asGenreModels() = map(Genre::asGenreModel)

private fun Int.asGenre() = Genre[NetworkGenreWithId[this].genreName]
private fun Int.asGenreModel() = com.zobaer53.zedmovies.domain.model.GenreModel[NetworkGenreWithId[this].genreName]
