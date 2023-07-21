
package com.zobaer53.zedmovies.data.common.result

import kotlinx.coroutines.flow.Flow

interface zedMoviesResultHandler<T> {
    fun onLoading(block: (T?) -> Unit)
    fun onSuccess(block: (T) -> Unit)
    fun onFailure(block: (Throwable) -> Unit)
}

fun <T> ZedMoviesResult<T>.handle(builder: zedMoviesResultHandler<T>.() -> Unit) {
    val resultHandler = object : zedMoviesResultHandler<T> {
        override fun onLoading(block: (T?) -> Unit) {
            if (isLoading()) block(value)
        }

        override fun onSuccess(block: (T) -> Unit) {
            if (isSuccess()) block(value)
        }

        override fun onFailure(block: (Throwable) -> Unit) {
            if (isFailure()) block(error)
        }
    }

    builder(resultHandler)
}

suspend fun <T> Flow<ZedMoviesResult<T>>.handle(builder: zedMoviesResultHandler<T>.() -> Unit) =
    collect { result -> result.handle(builder = builder) }
