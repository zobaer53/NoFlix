package com.zobaer53.zedmovies.data.common.result

sealed interface zedMoviesResult<out T> {
    data class Loading<T>(val value: T? = null) : zedMoviesResult<T> {
        override fun toString(): String = "Loading($value)"
    }

    sealed interface Success<out T> : zedMoviesResult<T> {
        val value: T

        class Value<T>(override val value: T) : Success<T> {
            override fun toString(): String = "Value($value)"
        }

        data class HttpResponse<T>(
            override val value: T,
            override val statusCode: Int,
            override val statusMessage: String?,
            override val url: String?
        ) : Success<T>, com.zobaer53.zedmovies.data.common.result.HttpResponse {
            override fun toString(): String =
                "HttpResponse($value, $statusCode, $statusMessage, $url)"
        }
    }

    sealed interface Failure<out T> : zedMoviesResult<T> {
        val error: Throwable
        val value: T?

        data class Error<T>(override val error: Throwable, override val value: T? = null) :
            Failure<T> {
            override fun toString(): String = "Error($error, $value)"
        }

        data class HttpError<T>(override val error: HttpException, override val value: T? = null) :
            Failure<T>, HttpResponse {
            override val statusCode: Int = error.statusCode
            override val statusMessage: String? = error.statusMessage
            override val url: String? = error.url

            override fun toString(): String = "HttpError($error, $value)"
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
