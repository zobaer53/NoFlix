

package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.core.domain.model.WishlistModel
import com.zobaer53.zedmovies.core.domain.repository.MovieDetailsRepository
import com.zobaer53.zedmovies.core.domain.repository.WishlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetWishlistMoviesUseCase @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val movieDetailsRepository: MovieDetailsRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = wishlistRepository.getMovies().flatMapLatest { wishlistMovies ->
        val ids = wishlistMovies.map(WishlistModel::id)
        movieDetailsRepository.getByIds(ids)
    }
}
