

package com.zobaer53.zedmovies.feature.details

sealed interface DetailsEvent {
    object WishlistMovie : DetailsEvent
    object WishlistTvShow : DetailsEvent

    object Refresh : DetailsEvent
    object Retry : DetailsEvent
    object ClearError : DetailsEvent
    object ClearUserMessage : DetailsEvent
}
