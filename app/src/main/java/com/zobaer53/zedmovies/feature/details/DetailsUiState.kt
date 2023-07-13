
package com.zobaer53.zedmovies.feature.details

import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.MovieDetails
import com.zobaer53.zedmovies.core.model.TvShowDetails
import com.zobaer53.zedmovies.core.model.UserMessage

data class DetailsUiState(
    val mediaType: MediaType.Details,
    val movie: MovieDetails? = null,
    val tvShow: TvShowDetails? = null,
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)
