
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.core.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(query: String) = repository.search(query)
}
