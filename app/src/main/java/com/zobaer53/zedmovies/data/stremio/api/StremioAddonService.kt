package com.zobaer53.zedmovies.data.stremio.api

import com.zobaer53.zedmovies.data.stremio.model.AddonManifest
import com.zobaer53.zedmovies.data.stremio.model.AddonStreamResponse
import com.zobaer53.zedmovies.data.stremio.model.ParsedStream
import com.zobaer53.zedmovies.data.stremio.model.StreamKind
import com.zobaer53.zedmovies.data.stremio.model.StremioStream
import com.zobaer53.zedmovies.data.stremio.parser.QualityParser
import com.zobaer53.zedmovies.data.stremio.parser.SizeParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StremioAddonService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    companion object {
        private val SEEDERS_REGEX = Regex("[👤👥]\\s*(\\d+)")
        const val DEFAULT_TORRENTIO_MANIFEST = "https://torrentio.strem.fun/manifest.json"
    }

    fun normalizeAddonBase(manifestUrl: String): String {
        return manifestUrl.trim()
            .replace(Regex("^stremio://", RegexOption.IGNORE_CASE), "https://")
            .replace(Regex("/manifest\\.json$", RegexOption.IGNORE_CASE), "")
    }

    suspend fun fetchManifest(manifestUrl: String): Result<AddonManifest> = withContext(Dispatchers.IO) {
        runCatching {
            val base = normalizeAddonBase(manifestUrl)
            val fullUrl = "$base/manifest.json"
            val request = Request.Builder()
                .url(fullUrl)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Failed to load manifest: HTTP ${response.code}")
                }
                val body = response.body.string()
                json.decodeFromString<AddonManifest>(body)
            }
        }
    }

    suspend fun fetchStreamsFromAddon(
        manifestUrl: String,
        type: String,
        id: String
    ): Result<List<ParsedStream>> = withContext(Dispatchers.IO) {
        runCatching {
            val base = normalizeAddonBase(manifestUrl)
            val manifest = fetchManifest(manifestUrl).getOrNull()
                ?: throw IllegalStateException("Could not load addon manifest")

            if (!manifest.supportsStreams(type, id)) {
                return@runCatching emptyList()
            }

            val addonName = manifest.name.ifBlank { base }
            val encodedId = URLEncoder.encode(id, "UTF-8")
            val streamUrl = "$base/stream/$type/$encodedId.json"

            val clientWithTimeout = okHttpClient.newBuilder()
                .readTimeout(25, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(streamUrl)
                .build()

            clientWithTimeout.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Stream request failed: HTTP ${response.code}")
                }
                val body = response.body.string()
                val streamResponse = json.decodeFromString<AddonStreamResponse>(body)

                streamResponse.streams
                    .filter { it.url != null || it.infoHash != null || it.ytId != null }
                    .map { parseStream(it, addonName) }
            }
        }
    }

    suspend fun collectAllStreams(
        manifestUrls: List<String>,
        type: String,
        id: String
    ): Pair<List<ParsedStream>, List<String>> = coroutineScope {
        val errors = mutableListOf<String>()
        val deferredStreams = manifestUrls.map { url ->
            async {
                val result = fetchStreamsFromAddon(url, type, id)
                if (result.isFailure) {
                    synchronized(errors) {
                        errors.add("$url: ${result.exceptionOrNull()?.message}")
                    }
                    emptyList()
                } else {
                    result.getOrDefault(emptyList())
                }
            }
        }

        val allStreams = deferredStreams.awaitAll().flatten()

        // Sort by quality rank, then by size descending
        val sorted = allStreams.sortedWith(
            compareBy<ParsedStream> { it.quality.rank }
                .thenByDescending { it.sizeBytes ?: 0L }
        )

        Pair(sorted, errors)
    }

    fun parseStream(stream: StremioStream, addonName: String): ParsedStream {
        val fullText = listOfNotNull(stream.name, stream.title, stream.description).joinToString(" ")
        val quality = QualityParser.parse(fullText)
        val sizeBytes = SizeParser.parse(fullText, stream.behaviorHints?.videoSize)
        val formattedSize = SizeParser.format(sizeBytes)

        val kind = when {
            !stream.infoHash.isNullOrBlank() -> StreamKind.TORRENT
            !stream.ytId.isNullOrBlank() -> StreamKind.YOUTUBE
            stream.url != null && stream.url.contains(".m3u8", ignoreCase = true) -> StreamKind.HLS
            else -> StreamKind.HTTP
        }

        val seedersMatch = SEEDERS_REGEX.find(fullText)
        val seeders = seedersMatch?.groupValues?.get(1)?.toIntOrNull()

        val displayText = stream.title?.ifBlank { null }
            ?: stream.description?.ifBlank { null }
            ?: stream.name
            ?: "Stream"

        return ParsedStream(
            rawStream = stream,
            addonName = addonName,
            quality = quality,
            displayText = displayText,
            sizeBytes = sizeBytes,
            formattedSize = formattedSize,
            kind = kind,
            seeders = seeders,
            infoHash = stream.infoHash?.lowercase(),
            fileIdx = stream.fileIdx,
            url = stream.url,
            filename = stream.behaviorHints?.filename,
            proxyHeaders = stream.behaviorHints?.proxyHeaders?.request ?: emptyMap()
        )
    }
}
