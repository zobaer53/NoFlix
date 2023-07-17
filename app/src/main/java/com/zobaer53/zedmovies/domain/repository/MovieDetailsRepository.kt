
package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.core.domain.model.MovieDetailsModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailsRepository {
    fun getById(id: Int): Flow<zedMoviesResult<MovieDetailsModel?>>
    fun getByIds(ids: List<Int>): Flow<zedMoviesResult<List<MovieDetailsModel>>>
}
