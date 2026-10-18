package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import com.zobaer53.zedmovies.domain.repository.WatchProgressRepository
import javax.inject.Inject

class SaveSiteWatchProgressUseCase @Inject constructor(
    private val repository: WatchProgressRepository
) {
    suspend operator fun invoke(progress: List<LastWatchedEpisodeModel>) =
        repository.saveSiteProgress(progress)
}
