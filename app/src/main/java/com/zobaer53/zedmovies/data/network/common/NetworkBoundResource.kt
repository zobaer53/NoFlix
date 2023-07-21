

package com.zobaer53.zedmovies.data.network.common

import android.util.Log
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.data.network.util.MESSAGE_UNHANDLED_STATE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> ZedMoviesResult<RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<ZedMoviesResult<ResultType>> = flow {
    emit(ZedMoviesResult.loading())
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(ZedMoviesResult.Loading(data))
        val response = fetch()

        when {
            response.isSuccess() -> {
                Log.i("Success1","tv details id ${response.value} ")
                saveFetchResult(response.value)
                query().map { ZedMoviesResult.success(it) }
            }
            response.isFailure() -> {
                Log.i("Success1","tv details error  ${response.error} ")

                val throwable = response.error
                query().map { ZedMoviesResult.failure(throwable, it) }
            }
            else ->
                error("$MESSAGE_UNHANDLED_STATE $response")
        }
    } else {
        query().map { ZedMoviesResult.success(it) }
    }

    emitAll(flow)
}
