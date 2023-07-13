

package com.zobaer53.zedmovies.feature.list

import androidx.paging.PagingData
import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.Movie
import com.zobaer53.zedmovies.core.model.TvShow
import kotlinx.coroutines.flow.Flow

data class ListUiState(
    val mediaType: MediaType.Common,
    val movies: Flow<PagingData<Movie>>,
    val tvShows: Flow<PagingData<TvShow>>
)
