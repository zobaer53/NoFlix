
package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.core.domain.model.TvShowDetailsModel
import kotlinx.coroutines.flow.Flow

interface TvShowDetailsRepository {
    fun getById(id: Int): Flow<zedMoviesResult<TvShowDetailsModel?>>
    fun getByIds(ids: List<Int>): Flow<zedMoviesResult<List<TvShowDetailsModel>>>
}
