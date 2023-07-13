

package com.zobaer53.zedmovies.feature.wishlist

import com.zobaer53.zedmovies.core.model.MovieDetails
import com.zobaer53.zedmovies.core.model.TvShowDetails

data class WishlistUiState(
    val movies: List<MovieDetails> = emptyList(),
    val tvShows: List<TvShowDetails> = emptyList(),
    val isMoviesLoading: Boolean = false,
    val isTvShowsLoading: Boolean = false,
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)
