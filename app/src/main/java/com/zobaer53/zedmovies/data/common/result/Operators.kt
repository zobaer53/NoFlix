
package com.zobaer53.zedmovies.data.common.result

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> zedMoviesResult<T>.isLoading(): Boolean {
    contract {
        returns() implies (this@isLoading is zedMoviesResult.Loading<T>)
    }
    return this is zedMoviesResult.Loading<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> zedMoviesResult<T>.isSuccess(): Boolean {
    contract {
        returns() implies (this@isSuccess is zedMoviesResult.Success<T>)
    }
    return this is zedMoviesResult.Success<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> zedMoviesResult<T>.isFailure(): Boolean {
    contract {
        returns() implies (this@isFailure is zedMoviesResult.Failure<T>)
    }
    return this is zedMoviesResult.Failure<T>
}
