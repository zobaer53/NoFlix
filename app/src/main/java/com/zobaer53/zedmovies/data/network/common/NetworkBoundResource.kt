

package com.zobaer53.zedmovies.data.network.common

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.core.network.util.MESSAGE_UNHANDLED_STATE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> zedMoviesResult<RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<zedMoviesResult<ResultType>> = flow {
    emit(zedMoviesResult.loading())
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(zedMoviesResult.Loading(data))
        val response = fetch()

        when {
            response.isSuccess() -> {
                saveFetchResult(response.value)
                query().map { zedMoviesResult.success(it) }
            }
            response.isFailure() -> {
                val throwable = response.error
                query().map { zedMoviesResult.failure(throwable, it) }
            }
            else -> error("$MESSAGE_UNHANDLED_STATE $response")
        }
    } else {
        query().map { zedMoviesResult.success(it) }
    }

    emitAll(flow)
}
