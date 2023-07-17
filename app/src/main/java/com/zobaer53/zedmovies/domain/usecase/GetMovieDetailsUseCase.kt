

package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.core.domain.repository.MovieDetailsRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(private val repository: MovieDetailsRepository) {
    operator fun invoke(id: Int) = repository.getById(id)
}
