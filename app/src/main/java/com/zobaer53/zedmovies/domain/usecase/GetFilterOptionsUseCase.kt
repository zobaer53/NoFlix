package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.repository.FilterOptionsRepository
import javax.inject.Inject

class GetFilterOptionsUseCase @Inject constructor(private val repository: FilterOptionsRepository) {
    suspend operator fun invoke() = repository.getFilterOptions()
}
