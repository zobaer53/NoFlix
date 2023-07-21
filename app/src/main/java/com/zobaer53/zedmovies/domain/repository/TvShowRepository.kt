

package com.zobaer53.zedmovies.domain.repository

import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.domain.model.TvShowModel
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    fun getByMediaType(mediaTypeModel: MediaTypeModel.TvShow): Flow<ZedMoviesResult<List<TvShowModel>>>
    fun getPagingByMediaType(mediaTypeModel: MediaTypeModel.TvShow): Flow<PagingData<TvShowModel>>
    fun search(query: String): Flow<PagingData<TvShowModel>>
}
