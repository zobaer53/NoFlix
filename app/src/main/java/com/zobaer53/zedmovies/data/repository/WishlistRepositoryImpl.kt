

package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.mapper.asWishlistModel
import com.zobaer53.zedmovies.data.mapper.listMap
import com.zobaer53.zedmovies.data.database.model.wishlist.WishlistEntity
import com.zobaer53.zedmovies.data.database.source.WishlistDatabaseDataSource
import com.zobaer53.zedmovies.domain.model.WishlistModel
import com.zobaer53.zedmovies.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val databaseDataSource: WishlistDatabaseDataSource
) : WishlistRepository {
    override fun getMovies(): Flow<List<WishlistModel>> =
        databaseDataSource.getMovies().listMap(WishlistEntity::asWishlistModel)

    override fun getTvShows(): Flow<List<WishlistModel>> =
        databaseDataSource.getTvShows().listMap(WishlistEntity::asWishlistModel)

    override suspend fun addMovieToWishlist(id: Int) = databaseDataSource.addMovieToWishlist(id)

    override suspend fun addTvShowToWishlist(id: Int) = databaseDataSource.addTvShowToWishlist(id)

    override suspend fun removeMovieFromWishlist(id: Int) =
        databaseDataSource.removeMovieFromWishlist(id)

    override suspend fun removeTvShowFromWishlist(id: Int) =
        databaseDataSource.removeTvShowFromWishlist(id)
}
