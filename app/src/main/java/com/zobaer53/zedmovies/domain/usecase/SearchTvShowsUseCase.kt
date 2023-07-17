

package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.repository.TvShowRepository
import javax.inject.Inject

class SearchTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) {
    operator fun invoke(query: String) = repository.search(query)
}
