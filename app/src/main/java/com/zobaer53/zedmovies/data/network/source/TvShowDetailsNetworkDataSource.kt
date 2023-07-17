
package com.zobaer53.zedmovies.data.network.source

import android.util.Log
import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.core.network.api.service.TvShowService
import com.zobaer53.zedmovies.core.network.model.tvshow.NetworkTvShowDetails
import com.zobaer53.zedmovies.core.network.util.Constants.Fields.DETAILS_APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.core.network.util.MESSAGE_UNHANDLED_STATE
import javax.inject.Inject

class TvShowDetailsNetworkDataSource @Inject constructor(private val tvShowService: TvShowService) {
    suspend fun getById(
        id: Int,
        language: String,
        appendToResponse: String = DETAILS_APPEND_TO_RESPONSE
    ): zedMoviesResult<NetworkTvShowDetails> =
        tvShowService.getDetailsById(id, language, appendToResponse)

    suspend fun getByIds(
        ids: List<Int>,
        language: String,
        appendToResponse: String = DETAILS_APPEND_TO_RESPONSE
    ): zedMoviesResult<List<NetworkTvShowDetails>> {
        val tvShows = ids.map { id ->
            val response = tvShowService.getDetailsById(id, language, appendToResponse)

            Log.i("tvDetails1","tv details id $id ")
            when {
                response.isSuccess() -> response.value
                response.isFailure() ->
                    return zedMoviesResult.failure(response.error)
                else ->
                    error("$MESSAGE_UNHANDLED_STATE $response")
            }
        }

        return zedMoviesResult.success(tvShows)
    }
}
