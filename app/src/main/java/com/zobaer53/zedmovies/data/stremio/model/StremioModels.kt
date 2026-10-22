package com.zobaer53.zedmovies.data.stremio.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class AddonManifest(
    val id: String,
    val name: String,
    val version: String? = null,
    val description: String? = null,
    val resources: List<JsonElement> = emptyList(),
    val types: List<String> = emptyList(),
    val idPrefixes: List<String> = emptyList()
) {
    /**
     * Checks if this addon supports resolving streams for a given media type and ID prefix.
     */
    fun supportsStreams(type: String, id: String): Boolean {
        if (resources.isEmpty()) return true
        return resources.any { res ->
            when (res) {
                is JsonPrimitive -> {
                    res.content == "stream" &&
                        (types.isEmpty() || types.contains(type)) &&
                        (idPrefixes.isEmpty() || idPrefixes.any { id.startsWith(it) })
                }
                is JsonObject -> {
                    val name = res["name"]?.jsonPrimitive?.content
                    val resTypes = res["types"]?.jsonArray?.map { it.jsonPrimitive.content }
                    val resPrefixes = res["idPrefixes"]?.jsonArray?.map { it.jsonPrimitive.content }

                    name == "stream" &&
                        (resTypes == null || resTypes.contains(type)) &&
                        (resPrefixes == null || resPrefixes.any { id.startsWith(it) })
                }
                else -> false
            }
        }
    }
}

@Serializable
data class AddonStreamResponse(
    val streams: List<StremioStream> = emptyList()
)

@Serializable
data class StremioStream(
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val ytId: String? = null,
    val sources: List<String> = emptyList(),
    val behaviorHints: StreamBehaviorHints? = null
)

@Serializable
data class StreamBehaviorHints(
    val filename: String? = null,
    val videoSize: Long? = null,
    val proxyHeaders: StreamProxyHeaders? = null
)

@Serializable
data class StreamProxyHeaders(
    val request: Map<String, String> = emptyMap()
)

enum class StreamKind(val label: String) {
    TORRENT("Torrent"),
    HLS("HLS"),
    HTTP("Direct HTTP"),
    YOUTUBE("YouTube")
}

enum class StreamQuality(val label: String, val rank: Int) {
    Q_4K("4K", 0),
    Q_1440P("1440p", 1),
    Q_1080P("1080p", 2),
    Q_720P("720p", 3),
    Q_576P("576p", 4),
    Q_480P("480p", 5),
    Q_360P("360p", 6),
    UNKNOWN("Unknown", 99)
}

data class ParsedStream(
    val rawStream: StremioStream,
    val addonName: String,
    val quality: StreamQuality,
    val displayText: String,
    val sizeBytes: Long?,
    val formattedSize: String,
    val kind: StreamKind,
    val seeders: Int? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val url: String? = null,
    val filename: String? = null,
    val proxyHeaders: Map<String, String> = emptyMap()
)

data class HlsVariant(
    val url: String,
    val bandwidth: Long?,
    val resolution: String?,
    val quality: String
)

data class TorrentFileInfo(
    val idx: Int,
    val name: String,
    val length: Long
)

data class TorrentInspectResult(
    val name: String,
    val infoHash: String,
    val sources: List<String>,
    val files: List<TorrentFileInfo> = emptyList(),
    val suggestedIdx: Int? = null
)

sealed class InspectResult {
    data class Torrent(val info: TorrentInspectResult) : InspectResult()
    data class Hls(val url: String, val variants: List<HlsVariant>) : InspectResult()
    data class Http(val url: String) : InspectResult()
}

