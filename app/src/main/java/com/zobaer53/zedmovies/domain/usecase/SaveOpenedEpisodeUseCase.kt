package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.repository.WatchProgressRepository
import javax.inject.Inject

class SaveOpenedEpisodeUseCase @Inject constructor(
    private val repository: WatchProgressRepository
) {
    suspend operator fun invoke(tvShowId: Int, season: Int, episode: Int) =
        repository.saveOpenedEpisode(tvShowId, season, episode)
}
