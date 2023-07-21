
package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.domain.model.TvShowDetailsModel
import kotlinx.coroutines.flow.Flow

interface TvShowDetailsRepository {
    fun getById(id: Int): Flow<ZedMoviesResult<TvShowDetailsModel?>>
    fun getByIds(ids: List<Int>): Flow<ZedMoviesResult<List<TvShowDetailsModel>>>
}
