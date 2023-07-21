package com.zobaer53.zedmovies.data.common.result

import android.util.Log

sealed interface ZedMoviesResult<out T> {
    data class Loading<T>(val value: T? = null) : ZedMoviesResult<T> {
        override fun toString(): String = "Loading($value)"
    }

    sealed interface Success<out T> : ZedMoviesResult<T> {
        val value: T

        class Value<T>(override val value: T) : Success<T> {
            override fun toString(): String {
               Log.i("Success1","Success1: $value")
                return "Value($value)"
            }
        }

        data class HttpResponse<T>(
            override val value: T,
            override val statusCode: Int,
            override val statusMessage: String?,
            override val url: String?
        ) : Success<T>, com.zobaer53.zedmovies.data.common.result.HttpResponse {
            override fun toString(): String {
                Log.i("Success1","Success1: $value, StatusCode: $statusCode, StatusMessage: $statusMessage, URL: $url")
                return "HttpResponse($value, $statusCode, $statusMessage, $url)"
            }
        }
    }

    sealed interface Failure<out T> : ZedMoviesResult<T> {
        val error: Throwable
        val value: T?

        data class Error<T>(override val error: Throwable, override val value: T? = null) :
            Failure<T> {
            override fun toString(): String {
                Log.i("Success1","error: $value")
               return "Error($error, $value)"
            }
        }

        data class HttpError<T>(override val error: HttpException, override val value: T? = null) :
            Failure<T>, HttpResponse {
            override val statusCode: Int = error.statusCode
            override val statusMessage: String? = error.statusMessage
            override val url: String? = error.url

            override fun toString(): String {

                Log.i("Success1","error: $value")
                return "HttpError($error, $value)"
            }
        }
    }

    companion object {
        fun <T> loading(value: T? = null) = Loading(value)

        fun <T> success(value: T) = Success.Value(value)
        fun <T> success(value: T, statusCode: Int, statusMessage: String?, url: String?) =
            Success.HttpResponse(value, statusCode, statusMessage, url)

        fun <T> failure(error: Throwable, value: T? = null) = Failure.Error(error, value)
        fun <T> failure(error: HttpException, value: T? = null) = Failure.HttpError(error, value)
    }
}
