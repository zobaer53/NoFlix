

package com.zobaer53.zedmovies.data.network.retrofit

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ResultAdapterFactory : CallAdapter.Factory() {
    @Suppress("ReturnCount")
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawReturnType: Class<*> = getRawType(returnType)
        if (rawReturnType == Call::class.java && returnType is ParameterizedType) {
            val callInnerType: Type = getParameterUpperBound(0, returnType)
            if (getRawType(callInnerType) == zedMoviesResult::class.java) {
                if (callInnerType is ParameterizedType) {
                    val resultInnerType = getParameterUpperBound(0, callInnerType)
                    return ResultCallAdapter<Any?>(resultInnerType)
                }
                return ResultCallAdapter<Nothing>(Nothing::class.java)
            }
        }

        return null
    }
}

private class ResultCallAdapter<T>(private val type: Type) :
    CallAdapter<T, Call<zedMoviesResult<T>>> {
    override fun responseType(): Type = type
    override fun adapt(call: Call<T>): Call<zedMoviesResult<T>> = ResultCall(call)
}
