

package com.zobaer53.zedmovies.ui.wishlist

import com.zobaer53.zedmovies.data.model.MovieDetails
import com.zobaer53.zedmovies.data.model.TvShowDetails

data class WishlistUiState(
    val movies: List<MovieDetails> = emptyList(),
    val tvShows: List<TvShowDetails> = emptyList(),
    val isMoviesLoading: Boolean = false,
    val isTvShowsLoading: Boolean = false,
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)
