

package com.zobaer53.zedmovies.di

import com.zobaer53.zedmovies.core.data.repository.MovieDetailsRepositoryImpl
import com.zobaer53.zedmovies.core.data.repository.MovieRepositoryImpl
import com.zobaer53.zedmovies.core.data.repository.TvShowDetailsRepositoryImpl
import com.zobaer53.zedmovies.core.data.repository.TvShowRepositoryImpl
import com.zobaer53.zedmovies.core.data.repository.WishlistRepositoryImpl
import com.zobaer53.zedmovies.core.domain.repository.MovieDetailsRepository
import com.zobaer53.zedmovies.core.domain.repository.MovieRepository
import com.zobaer53.zedmovies.core.domain.repository.TvShowDetailsRepository
import com.zobaer53.zedmovies.core.domain.repository.TvShowRepository
import com.zobaer53.zedmovies.core.domain.repository.WishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    @ViewModelScoped
    fun bindMovieRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository

    @Binds
    @ViewModelScoped
    fun bindTvShowRepository(tvShowRepositoryImpl: TvShowRepositoryImpl): TvShowRepository

    @Binds
    @ViewModelScoped
    fun bindMovieDetailsRepository(
        movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl
    ): MovieDetailsRepository

    @Binds
    @ViewModelScoped
    fun bindTvShowDetailsRepository(
        tvShowDetailsRepositoryImpl: TvShowDetailsRepositoryImpl
    ): TvShowDetailsRepository

    @Binds
    @ViewModelScoped
    fun bindWishlistRepository(wishlistRepositoryImpl: WishlistRepositoryImpl): WishlistRepository


}
