
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.core.domain.repository.WishlistRepository
import javax.inject.Inject

class AddTvShowToWishlistUseCase @Inject constructor(private val repository: WishlistRepository) {
    suspend operator fun invoke(id: Int) = repository.addTvShowToWishlist(id)
}