package com.zobaer53.zedmovies.ui.webview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zobaer53.zedmovies.domain.usecase.GetLastWatchedEpisodeUseCase
import com.zobaer53.zedmovies.domain.usecase.SaveOpenedEpisodeUseCase
import com.zobaer53.zedmovies.domain.usecase.SaveSiteWatchProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Remembers which episode of a TV show the player is on, so Play can resume it next time. */
@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val getLastWatchedEpisodeUseCase: GetLastWatchedEpisodeUseCase,
    private val saveOpenedEpisodeUseCase: SaveOpenedEpisodeUseCase,
    private val saveSiteWatchProgressUseCase: SaveSiteWatchProgressUseCase
) : ViewModel() {
    private var lastOpenedEpisode: StreamEpisode? = null

    suspend fun getLastWatchedEpisode(tvShowId: Int): StreamEpisode? =
        getLastWatchedEpisodeUseCase(tvShowId).first()?.let {
            StreamEpisode(
                tvShowId = it.tvShowId,
                season = it.season,
                episode = it.episode,
                positionSeconds = it.positionSeconds?.toInt()
            )
        }

    // URL changes repeat for reloads and history updates, so only a new episode is saved.
    fun onEpisodeOpened(episode: StreamEpisode) {
        if (episode == lastOpenedEpisode) return
        lastOpenedEpisode = episode
        save { saveOpenedEpisodeUseCase(episode.tvShowId, episode.season, episode.episode) }
    }

    fun onSiteWatchProgress(json: String) {
        parsePlaybackActiveSignal(json)?.let { signal ->
            android.util.Log.i(
                "NoFlixPlayback",
                "Playback verified: video ${signal.videoWidth}x${signal.videoHeight}, rendered ${signal.renderedWidth}x${signal.renderedHeight}px at ${signal.currentTime}s"
            )
            return
        }
        val progress = parseStreamWatchProgress(json)
        if (progress.isNotEmpty()) save { saveSiteWatchProgressUseCase(progress) }
    }

    // The last update often arrives as the player closes: finish the write even after onCleared.
    private fun save(block: suspend () -> Unit) {
        viewModelScope.launch { withContext(NonCancellable) { block() } }
    }
}
