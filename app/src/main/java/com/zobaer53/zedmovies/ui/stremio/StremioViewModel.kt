package com.zobaer53.zedmovies.ui.stremio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaDetails
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaSummary
import com.zobaer53.zedmovies.data.stremio.model.CinemetaVideo
import com.zobaer53.zedmovies.data.stremio.model.DownloadJob
import com.zobaer53.zedmovies.data.stremio.model.HlsVariant
import com.zobaer53.zedmovies.data.stremio.model.InspectResult
import com.zobaer53.zedmovies.data.stremio.model.ParsedStream
import com.zobaer53.zedmovies.data.stremio.model.StreamKind
import com.zobaer53.zedmovies.data.stremio.repository.StremioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StremioTab(val title: String) {
    SEARCH("Search"),
    PASTE_LINK("Paste link"),
    DOWNLOADS("Downloads"),
    SETTINGS("Settings")
}

data class StremioUiState(
    val currentTab: StremioTab = StremioTab.SEARCH,
    val searchQuery: String = "",
    val searchType: String? = null, // null = all, "movie", "series"
    val isSearching: Boolean = false,
    val searchResults: List<CinemetaMetaSummary> = emptyList(),
    val searchError: String? = null,

    // Detail & streams
    val selectedMeta: CinemetaMetaDetails? = null,
    val isLoadingDetails: Boolean = false,
    val selectedSeason: Int? = null,
    val selectedEpisode: CinemetaVideo? = null,
    val isLoadingStreams: Boolean = false,
    val streams: List<ParsedStream> = emptyList(),
    val streamErrors: List<String> = emptyList(),
    val qualityFilter: String = "All",
    val activeStreamTitle: String = "",

    // HLS dialog
    val hlsTargetStream: ParsedStream? = null,
    val hlsVariants: List<HlsVariant> = emptyList(),
    val isLoadingHlsVariants: Boolean = false,

    // Paste link
    val linkInput: String = "",
    val linkTitle: String = "",
    val isInspecting: Boolean = false,
    val inspectResult: InspectResult? = null,
    val inspectError: String? = null,

    // Settings
    val newAddonInput: String = "",
    val isAddingAddon: Boolean = false,
    val settingsMessage: String? = null
)

@HiltViewModel
class StremioViewModel @Inject constructor(
    private val repository: StremioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StremioUiState())
    val uiState: StateFlow<StremioUiState> = _uiState.asStateFlow()

    val downloads: StateFlow<List<DownloadJob>> = repository.downloadJobsFlow

    val addons: StateFlow<List<String>> = repository.addonsFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf(com.zobaer53.zedmovies.data.stremio.api.StremioAddonService.DEFAULT_TORRENTIO_MANIFEST)
        )

    val downloadDir: StateFlow<String> = repository.downloadDirFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun selectTab(tab: StremioTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSearchTypeChanged(type: String?) {
        _uiState.update { it.copy(searchType = type) }
        if (_uiState.value.searchQuery.isNotBlank()) {
            search()
        }
    }

    fun search() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    searchError = null,
                    selectedMeta = null
                )
            }
            val result = repository.searchCinemeta(query, _uiState.value.searchType)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = result.getOrDefault(emptyList()),
                        searchError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchError = result.exceptionOrNull()?.message ?: "Search failed"
                    )
                }
            }
        }
    }

    fun openMeta(type: String, id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetails = true, streams = emptyList(), selectedMeta = null) }
            val result = repository.getCinemetaDetails(type, id)
            if (result.isSuccess) {
                val meta = result.getOrThrow()
                val seasons = meta.videos.map { it.season }.filter { it > 0 }.distinct().sorted()
                val firstSeason = seasons.firstOrNull()
                val firstEpisode = meta.videos.firstOrNull { it.season == firstSeason }

                _uiState.update {
                    it.copy(
                        isLoadingDetails = false,
                        selectedMeta = meta,
                        selectedSeason = firstSeason,
                        selectedEpisode = firstEpisode
                    )
                }

                if (type == "movie") {
                    val title = "${meta.name}${meta.year?.let { y -> " ($y)" } ?: ""}"
                    loadStreams("movie", id, title)
                } else if (firstEpisode != null) {
                    val sNum = String.format("%02d", firstEpisode.season)
                    val eNum = String.format("%02d", firstEpisode.episode)
                    val title = "${meta.name} S${sNum}E${eNum}"
                    loadStreams("series", firstEpisode.id, title)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingDetails = false,
                        searchError = result.exceptionOrNull()?.message ?: "Failed to load details"
                    )
                }
            }
        }
    }

    fun closeDetails() {
        _uiState.update { it.copy(selectedMeta = null, streams = emptyList()) }
    }

    fun selectSeason(season: Int) {
        val meta = _uiState.value.selectedMeta ?: return
        val firstEpOfSeason = meta.videos.firstOrNull { it.season == season }
        _uiState.update {
            it.copy(
                selectedSeason = season,
                selectedEpisode = firstEpOfSeason
            )
        }
    }

    fun selectEpisode(episode: CinemetaVideo) {
        _uiState.update { it.copy(selectedEpisode = episode) }
        val meta = _uiState.value.selectedMeta ?: return
        val sNum = String.format("%02d", episode.season)
        val eNum = String.format("%02d", episode.episode)
        val title = "${meta.name} S${sNum}E${eNum}"
        loadStreams("series", episode.id, title)
    }

    fun loadStreams(type: String, id: String, title: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingStreams = true,
                    activeStreamTitle = title,
                    qualityFilter = "All"
                )
            }
            val (streams, errors) = repository.getStreams(type, id)
            _uiState.update {
                it.copy(
                    isLoadingStreams = false,
                    streams = streams,
                    streamErrors = errors
                )
            }
        }
    }

    fun setQualityFilter(filter: String) {
        _uiState.update { it.copy(qualityFilter = filter) }
    }

    fun onStreamAction(stream: ParsedStream) {
        val title = _uiState.value.activeStreamTitle.ifBlank { stream.filename ?: "video" }

        when (stream.kind) {
            StreamKind.TORRENT -> {
                stream.infoHash?.let { ih ->
                    repository.openMagnet(ih, title, stream.rawStream.sources)
                }
            }
            StreamKind.HLS -> {
                val url = stream.url ?: return
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            hlsTargetStream = stream,
                            isLoadingHlsVariants = true,
                            hlsVariants = emptyList()
                        )
                    }
                    val variantsResult = repository.fetchHlsVariants(url, stream.proxyHeaders)
                    _uiState.update {
                        it.copy(
                            isLoadingHlsVariants = false,
                            hlsVariants = variantsResult.getOrDefault(emptyList())
                        )
                    }
                }
            }
            StreamKind.HTTP -> {
                val url = stream.url ?: return
                viewModelScope.launch {
                    repository.startDownload(
                        title = title,
                        url = url,
                        headers = stream.proxyHeaders,
                        quality = stream.quality.label
                    )
                    _uiState.update { it.copy(currentTab = StremioTab.DOWNLOADS) }
                }
            }
            StreamKind.YOUTUBE -> {
                // Not supported on standard HTTP downloader
            }
        }
    }

    fun onHlsVariantSelected(variant: HlsVariant) {
        val stream = _uiState.value.hlsTargetStream ?: return
        val baseTitle = _uiState.value.activeStreamTitle.ifBlank { "video" }
        val title = "$baseTitle [${variant.quality}]"

        viewModelScope.launch {
            repository.startDownload(
                title = title,
                url = variant.url,
                headers = stream.proxyHeaders,
                quality = variant.quality
            )
            _uiState.update {
                it.copy(
                    hlsTargetStream = null,
                    hlsVariants = emptyList(),
                    currentTab = StremioTab.DOWNLOADS
                )
            }
        }
    }

    fun dismissHlsDialog() {
        _uiState.update { it.copy(hlsTargetStream = null, hlsVariants = emptyList()) }
    }

    // ---- Paste Link ----
    fun onLinkInputChanged(input: String) {
        _uiState.update { it.copy(linkInput = input) }
    }

    fun onLinkTitleChanged(title: String) {
        _uiState.update { it.copy(linkTitle = title) }
    }

    fun inspectLink() {
        val link = _uiState.value.linkInput.trim()
        if (link.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isInspecting = true, inspectResult = null, inspectError = null) }
            val result = repository.inspectLink(link)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isInspecting = false,
                        inspectResult = result.getOrNull(),
                        inspectError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isInspecting = false,
                        inspectError = result.exceptionOrNull()?.message ?: "Inspect failed"
                    )
                }
            }
        }
    }

    fun startDownloadFromInspect(url: String, quality: String? = null) {
        val title = _uiState.value.linkTitle.ifBlank { "video" }
        viewModelScope.launch {
            repository.startDownload(title = title, url = url, quality = quality)
            _uiState.update { it.copy(currentTab = StremioTab.DOWNLOADS) }
        }
    }

    // ---- Downloads ----
    fun cancelDownload(jobId: Long) = repository.cancelDownload(jobId)
    fun deleteJob(jobId: Long, deleteFile: Boolean = false) = repository.deleteJob(jobId, deleteFile)
    fun openDownloadedFile(jobId: Long) = repository.openDownloadedFile(jobId)
    fun openMagnet(infoHash: String, title: String? = null, sources: List<String> = emptyList()) =
        repository.openMagnet(infoHash, title, sources)

    // ---- Settings ----
    fun onNewAddonInputChanged(input: String) {
        _uiState.update { it.copy(newAddonInput = input, settingsMessage = null) }
    }

    fun addAddon() {
        val url = _uiState.value.newAddonInput.trim()
        if (url.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingAddon = true, settingsMessage = null) }
            val result = repository.addAddon(url)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddingAddon = false,
                        newAddonInput = "",
                        settingsMessage = "Added addon: ${result.getOrNull()}"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isAddingAddon = false,
                        settingsMessage = "Failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun removeAddon(manifestUrl: String) {
        viewModelScope.launch {
            repository.removeAddon(manifestUrl)
        }
    }
}

