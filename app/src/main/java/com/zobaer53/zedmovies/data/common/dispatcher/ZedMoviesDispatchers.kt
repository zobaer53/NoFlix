package com.zobaer53.zedmovies.data.common.dispatcher

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val zedMoviesDispatcher: zedMoviesDispatchers)

enum class zedMoviesDispatchers { Default, Main, Unconfined, IO }
