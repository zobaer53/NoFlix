

package com.zobaer53.zedmovies.feature.wishlist

sealed interface WishlistEvent {
    object RefreshMovies : WishlistEvent
    object RefreshTvShows : WishlistEvent
    object Retry : WishlistEvent
    object ClearError : WishlistEvent
}
