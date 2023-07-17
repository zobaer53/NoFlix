package com.zobaer53.zedmovies.data.common.dispatcher.di

import com.zobaer53.zedmovies.data.common.dispatcher.zedMoviesDispatchers.Default
import com.zobaer53.zedmovies.data.common.dispatcher.zedMoviesDispatchers.IO
import com.zobaer53.zedmovies.data.common.dispatcher.zedMoviesDispatchers.Main
import com.zobaer53.zedmovies.data.common.dispatcher.zedMoviesDispatchers.Unconfined
import com.zobaer53.zedmovies.data.common.dispatcher.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(Main)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Dispatcher(Unconfined)
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    @Provides
    @Dispatcher(IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
