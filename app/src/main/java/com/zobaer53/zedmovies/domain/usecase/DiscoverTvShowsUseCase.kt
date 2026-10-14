package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.repository.TvShowRepository
import javax.inject.Inject

class DiscoverTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) {
    operator fun invoke(filters: SearchFiltersModel) = repository.discover(filters)
}
