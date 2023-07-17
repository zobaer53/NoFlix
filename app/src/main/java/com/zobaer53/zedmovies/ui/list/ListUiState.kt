

package com.zobaer53.zedmovies.ui.list

import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.TvShow
import kotlinx.coroutines.flow.Flow

data class ListUiState(
    val mediaType: MediaType.Common,
    val movies: Flow<PagingData<Movie>>,
    val tvShows: Flow<PagingData<TvShow>>
)
