package com.zobaer53.zedmovies.data.stremio.api

import com.zobaer53.zedmovies.data.stremio.model.CinemetaCatalogResponse
import com.zobaer53.zedmovies.data.stremio.model.CinemetaDetailsResponse
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaDetails
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemetaService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    companion object {
        private const val BASE_URL = "https://v3-cinemeta.strem.io"
    }

    suspend fun search(query: String, type: String? = null): Result<List<CinemetaMetaSummary>> = withContext(Dispatchers.IO) {
        runCatching {
            val encodedQuery = URLEncoder.encode(query.trim(), "UTF-8")
            val typesToQuery = if (!type.isNullOrBlank()) listOf(type) else listOf("movie", "series")
            val results = mutableListOf<CinemetaMetaSummary>()

            for (t in typesToQuery) {
                val url = "$BASE_URL/catalog/$t/top/search=$encodedQuery.json"
                val request = Request.Builder().url(url).build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body.string()
                        if (body.isNotEmpty()) {
                            val parsed = json.decodeFromString<CinemetaCatalogResponse>(body)
                            results.addAll(parsed.metas)
                        }
                    }
                }
            }
            results
        }
    }

    suspend fun getMeta(type: String, id: String): Result<CinemetaMetaDetails> = withContext(Dispatchers.IO) {
        runCatching {
            val url = "$BASE_URL/meta/$type/$id.json"
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Cinemeta returned HTTP ${response.code}")
                }
                val body = response.body.string()
                val parsed = json.decodeFromString<CinemetaDetailsResponse>(body)
                parsed.meta
            }
        }
    }
}
