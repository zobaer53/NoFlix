package com.zobaer53.zedmovies.data.stremio.repository

import com.zobaer53.zedmovies.data.stremio.api.CinemetaService
import com.zobaer53.zedmovies.data.stremio.api.StremioAddonService
import com.zobaer53.zedmovies.data.stremio.download.StremioDownloadManager
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaDetails
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaSummary
import com.zobaer53.zedmovies.data.stremio.model.DownloadJob
import com.zobaer53.zedmovies.data.stremio.model.HlsVariant
import com.zobaer53.zedmovies.data.stremio.model.InspectResult
import com.zobaer53.zedmovies.data.stremio.model.ParsedStream
import com.zobaer53.zedmovies.data.stremio.model.StreamKind
import com.zobaer53.zedmovies.data.stremio.model.TorrentFileInfo
import com.zobaer53.zedmovies.data.stremio.model.TorrentInspectResult
import com.zobaer53.zedmovies.data.stremio.parser.HlsParser
import com.zobaer53.zedmovies.data.stremio.parser.MagnetParser
import com.zobaer53.zedmovies.data.stremio.preferences.StremioPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

interface StremioRepository {
    suspend fun searchCinemeta(query: String, type: String? = null): Result<List<CinemetaMetaSummary>>
    suspend fun getCinemetaDetails(type: String, id: String): Result<CinemetaMetaDetails>
    suspend fun getStreams(type: String, id: String): Pair<List<ParsedStream>, List<String>>
    suspend fun inspectLink(link: String): Result<InspectResult>
    suspend fun fetchHlsVariants(url: String, headers: Map<String, String> = emptyMap()): Result<List<HlsVariant>>
    suspend fun startDownload(
        title: String,
        url: String,
        headers: Map<String, String> = emptyMap(),
        quality: String? = null
    ): Long

    val addonsFlow: Flow<List<String>>
    val downloadDirFlow: Flow<String>
    val downloadJobsFlow: StateFlow<List<DownloadJob>>

    suspend fun addAddon(manifestUrl: String): Result<String>
    suspend fun removeAddon(manifestUrl: String)
    suspend fun setDownloadDir(path: String)
    fun cancelDownload(jobId: Long)
    fun deleteJob(jobId: Long, deleteFile: Boolean = false)
    fun openDownloadedFile(jobId: Long)
    fun openMagnet(infoHash: String, title: String? = null, sources: List<String> = emptyList())
}

@Singleton
class StremioRepositoryImpl @Inject constructor(
    private val cinemetaService: CinemetaService,
    private val addonService: StremioAddonService,
    private val preferences: StremioPreferences,
    private val downloadManager: StremioDownloadManager,
    private val okHttpClient: OkHttpClient
) : StremioRepository {

    override suspend fun searchCinemeta(query: String, type: String?): Result<List<CinemetaMetaSummary>> {
        return cinemetaService.search(query, type)
    }

    override suspend fun getCinemetaDetails(type: String, id: String): Result<CinemetaMetaDetails> {
        return cinemetaService.getMeta(type, id)
    }

    override suspend fun getStreams(type: String, id: String): Pair<List<ParsedStream>, List<String>> {
        val addons = preferences.addonsFlow.first()
        return addonService.collectAllStreams(addons, type, id)
    }

    override suspend fun inspectLink(link: String): Result<InspectResult> = withContext(Dispatchers.IO) {
        runCatching {
            val trimmed = link.trim()
            val infoHash = MagnetParser.extractInfoHash(trimmed)
            if (infoHash != null) {
                val trackers = MagnetParser.extractTrackers(trimmed)
                val sources = trackers + listOf("dht:$infoHash")
                val files = listOf(
                    TorrentFileInfo(idx = 0, name = "Main Video File", length = 0L)
                )
                return@runCatching InspectResult.Torrent(
                    TorrentInspectResult(
                        name = "Torrent: $infoHash",
                        infoHash = infoHash,
                        sources = sources,
                        files = files,
                        suggestedIdx = 0
                    )
                )
            }

            if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                throw IllegalArgumentException("Please paste a magnet link, 40-char info hash, or http(s) video URL")
            }

            if (trimmed.contains(".m3u8", ignoreCase = true)) {
                val variantsResult = fetchHlsVariants(trimmed)
                val variants = variantsResult.getOrDefault(emptyList())
                return@runCatching InspectResult.Hls(url = trimmed, variants = variants)
            }

            InspectResult.Http(url = trimmed)
        }
    }

    override suspend fun fetchHlsVariants(
        url: String,
        headers: Map<String, String>
    ): Result<List<HlsVariant>> = withContext(Dispatchers.IO) {
        runCatching {
            val reqBuilder = Request.Builder().url(url)
            headers.forEach { (k, v) -> reqBuilder.addHeader(k, v) }
            okHttpClient.newCall(reqBuilder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Failed to load HLS playlist: HTTP ${response.code}")
                }
                val content = response.body.string()
                HlsParser.parseVariants(content, url)
            }
        }
    }

    override suspend fun startDownload(
        title: String,
        url: String,
        headers: Map<String, String>,
        quality: String?
    ): Long {
        val targetDir = preferences.downloadDirFlow.first()
        return downloadManager.startHttpDownload(
            title = title,
            url = url,
            headers = headers,
            quality = quality,
            targetDir = targetDir
        )
    }

    override val addonsFlow: Flow<List<String>> = preferences.addonsFlow
    override val downloadDirFlow: Flow<String> = preferences.downloadDirFlow
    override val downloadJobsFlow: StateFlow<List<DownloadJob>> = downloadManager.jobsFlow

    override suspend fun addAddon(manifestUrl: String): Result<String> {
        val manifestResult = addonService.fetchManifest(manifestUrl)
        return if (manifestResult.isSuccess) {
            val manifest = manifestResult.getOrThrow()
            preferences.addAddon(manifestUrl)
            Result.success(manifest.name)
        } else {
            Result.failure(manifestResult.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }

    override suspend fun removeAddon(manifestUrl: String) {
        preferences.removeAddon(manifestUrl)
    }

    override suspend fun setDownloadDir(path: String) {
        preferences.saveDownloadDir(path)
    }

    override fun cancelDownload(jobId: Long) {
        downloadManager.cancelDownload(jobId)
    }

    override fun deleteJob(jobId: Long, deleteFile: Boolean) {
        downloadManager.deleteJob(jobId, deleteFile)
    }

    override fun openDownloadedFile(jobId: Long) {
        downloadManager.openDownloadedFile(jobId)
    }

    override fun openMagnet(infoHash: String, title: String?, sources: List<String>) {
        downloadManager.openMagnetLink(infoHash, title, sources)
    }
}
