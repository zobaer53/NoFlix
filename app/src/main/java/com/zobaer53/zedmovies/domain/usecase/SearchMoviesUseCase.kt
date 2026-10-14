
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(query: String, filters: SearchFiltersModel = SearchFiltersModel()) =
        repository.search(query, filters)
}
