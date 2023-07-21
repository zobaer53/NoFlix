
package com.zobaer53.zedmovies.data.network.source

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.data.network.api.service.MovieService
import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovieDetails
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.DETAILS_APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.data.network.util.MESSAGE_UNHANDLED_STATE
import javax.inject.Inject

class MovieDetailsNetworkDataSource @Inject constructor(private val movieService: MovieService) {
    suspend fun getById(
        id: Int,
        language: String,
        appendToResponse: String = DETAILS_APPEND_TO_RESPONSE
    ): ZedMoviesResult<NetworkMovieDetails> =
        movieService.getDetailsById(id, language, appendToResponse)

    suspend fun getByIds(
        ids: List<Int>,
        language: String,
        appendToResponse: String = DETAILS_APPEND_TO_RESPONSE
    ): ZedMoviesResult<List<NetworkMovieDetails>> {
        val movies = ids.map { id ->
            val response = movieService.getDetailsById(id, language, appendToResponse)

            when {
                response.isSuccess() -> response.value
                response.isFailure() -> return ZedMoviesResult.failure(response.error)
                else -> error("$MESSAGE_UNHANDLED_STATE $response")
            }
        }

        return ZedMoviesResult.success(movies)
    }
}
