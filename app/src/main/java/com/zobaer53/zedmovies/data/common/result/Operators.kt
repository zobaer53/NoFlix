
package com.zobaer53.zedmovies.data.common.result

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> ZedMoviesResult<T>.isLoading(): Boolean {
    contract {
        returns() implies (this@isLoading is ZedMoviesResult.Loading<T>)
    }
    return this is ZedMoviesResult.Loading<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> ZedMoviesResult<T>.isSuccess(): Boolean {
    contract {
        returns() implies (this@isSuccess is ZedMoviesResult.Success<T>)
    }
    return this is ZedMoviesResult.Success<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> ZedMoviesResult<T>.isFailure(): Boolean {
    contract {
        returns() implies (this@isFailure is ZedMoviesResult.Failure<T>)
    }
    return this is ZedMoviesResult.Failure<T>
}
