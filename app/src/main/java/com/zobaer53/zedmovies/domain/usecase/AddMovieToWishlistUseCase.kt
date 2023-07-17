
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.repository.WishlistRepository
import javax.inject.Inject

class AddMovieToWishlistUseCase @Inject constructor(private val repository: WishlistRepository) {
    suspend operator fun invoke(id: Int) = repository.addMovieToWishlist(id)
}
