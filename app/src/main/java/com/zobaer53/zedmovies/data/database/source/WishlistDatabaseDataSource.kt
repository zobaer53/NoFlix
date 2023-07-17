package com.zobaer53.zedmovies.data.database.source

import com.zobaer53.zedmovies.core.database.dao.wishlist.WishlistDao
import com.zobaer53.zedmovies.core.database.mapper.asWishlistEntity
import com.zobaer53.zedmovies.core.database.model.common.MediaType.Wishlist
import javax.inject.Inject

class WishlistDatabaseDataSource @Inject constructor(private val wishlistDao: WishlistDao) {
    fun getMovies() = wishlistDao.getByMediaType(Wishlist.Movie)
    fun getTvShows() = wishlistDao.getByMediaType(Wishlist.TvShow)

    suspend fun addMovieToWishlist(id: Int) =
        wishlistDao.insert(Wishlist.Movie.asWishlistEntity(id))

    suspend fun addTvShowToWishlist(id: Int) =
        wishlistDao.insert(Wishlist.TvShow.asWishlistEntity(id))

    suspend fun removeMovieFromWishlist(id: Int) =
        wishlistDao.deleteByMediaTypeAndNetworkId(Wishlist.Movie, id)

    suspend fun removeTvShowFromWishlist(id: Int) =
        wishlistDao.deleteByMediaTypeAndNetworkId(Wishlist.TvShow, id)

    suspend fun isMovieWishlisted(id: Int) = wishlistDao.isWishlisted(Wishlist.Movie, id)
    suspend fun isTvShowWishlisted(id: Int) = wishlistDao.isWishlisted(Wishlist.TvShow, id)
}
