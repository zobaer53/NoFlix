package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.domain.model.FilterOptionsModel

interface FilterOptionsRepository {
    /** For the device's region, or the US when TMDB has no data for it. Cached for the session. */
    suspend fun getFilterOptions(): ZedMoviesResult<FilterOptionsModel>
}
