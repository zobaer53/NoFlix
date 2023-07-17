
package com.zobaer53.zedmovies.domain.repository

import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.core.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.core.domain.model.MovieModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getByMediaType(mediaTypeModel: MediaTypeModel.Movie): Flow<zedMoviesResult<List<MovieModel>>>
    fun getPagingByMediaType(mediaTypeModel: MediaTypeModel.Movie): Flow<PagingData<MovieModel>>
    fun search(query: String): Flow<PagingData<MovieModel>>
}
