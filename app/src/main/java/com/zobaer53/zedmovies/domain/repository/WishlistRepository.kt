
package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.domain.model.WishlistModel
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getMovies(): Flow<List<WishlistModel>>
    fun getTvShows(): Flow<List<WishlistModel>>

    suspend fun addMovieToWishlist(id: Int)
    suspend fun addTvShowToWishlist(id: Int)

    suspend fun removeMovieFromWishlist(id: Int)
    suspend fun removeTvShowFromWishlist(id: Int)
}
