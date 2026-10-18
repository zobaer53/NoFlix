package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.repository.WatchProgressRepository
import javax.inject.Inject

class GetLastWatchedEpisodeUseCase @Inject constructor(
    private val repository: WatchProgressRepository
) {
    operator fun invoke(tvShowId: Int) = repository.getLastWatchedEpisode(tvShowId)
}
