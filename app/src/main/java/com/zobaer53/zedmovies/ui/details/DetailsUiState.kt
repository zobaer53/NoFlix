
package com.zobaer53.zedmovies.ui.details

import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.MovieDetails
import com.zobaer53.zedmovies.data.model.TvShowDetails
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel

data class DetailsUiState(
    val mediaType: MediaType.Details,
    val movie: MovieDetails? = null,
    val tvShow: TvShowDetails? = null,
    val lastWatchedEpisode: LastWatchedEpisodeModel? = null,
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)
