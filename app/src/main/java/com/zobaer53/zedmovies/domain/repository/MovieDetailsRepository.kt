
package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.domain.model.MovieDetailsModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailsRepository {
    fun getById(id: Int): Flow<ZedMoviesResult<MovieDetailsModel?>>
    fun getByIds(ids: List<Int>): Flow<ZedMoviesResult<List<MovieDetailsModel>>>
}
