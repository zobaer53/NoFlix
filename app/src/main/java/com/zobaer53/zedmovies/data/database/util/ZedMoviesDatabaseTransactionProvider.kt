
package com.zobaer53.zedmovies.data.database.util

import androidx.room.withTransaction
import com.zobaer53.zedmovies.data.database.zedMoviesDatabase
import javax.inject.Inject

class zedMoviesDatabaseTransactionProvider @Inject constructor(
    private val zedMoviesDatabase: zedMoviesDatabase
) {
    suspend fun <R> runWithTransaction(block: suspend () -> R) =
        zedMoviesDatabase.withTransaction(block)
}
