package io.github.edsuns.adfilter.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.github.edsuns.adfilter.impl.BinaryDataStore
import io.github.edsuns.adfilter.impl.Constants.KEY_DOWNLOADED_DATA
import io.github.edsuns.adfilter.impl.Constants.KEY_DOWNLOAD_URL
import io.github.edsuns.adfilter.impl.Constants.KEY_FILTER_ID
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Created by Edsuns@qq.com on 2021/1/1.
 */
internal class DownloadWorker(context: Context, params: WorkerParameters) : Worker(
    context,
    params
) {
    private val binaryDataStore = BinaryDataStore.of(applicationContext)

    override fun doWork(): Result {
        val id = inputData.getString(KEY_FILTER_ID) ?: return Result.failure()
        val url = inputData.getString(KEY_DOWNLOAD_URL) ?: return Result.failure()
        Timber.v("Start download: $url $id")
        try {
            val dataName = "_$id"
            if (!download(url, dataName)) {
                Timber.v("Failed to download: $url $id")
                return Result.failure(inputData)
            }
            return Result.success(
                workDataOf(
                    KEY_FILTER_ID to id,
                    KEY_DOWNLOADED_DATA to dataName
                )
            )
        } catch (e: IOException) {
            Timber.v(e, "Failed to download: $url $id")
        }
        return Result.failure(inputData)
    }

    /**
     * GETs [url] into the [dataName] file as UTF-8, streamed so the list never sits in memory.
     * Returns false on a non-2xx response.
     */
    private fun download(url: String, dataName: String): Boolean {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = TIMEOUT_MS
        connection.readTimeout = TIMEOUT_MS
        try {
            if (connection.responseCode !in 200..299) return false
            // convert to UTF-8 if needed
            val charset = charsetOf(connection.contentType) ?: StandardCharsets.UTF_8
            binaryDataStore.saveFile(dataName) { file ->
                connection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        if (charset == StandardCharsets.UTF_8) {
                            input.copyTo(output)
                        } else {
                            output.writer().use { input.reader(charset).copyTo(it) }
                        }
                    }
                }
            }
            return true
        } finally {
            connection.disconnect()
        }
    }

    private fun charsetOf(contentType: String?): Charset? {
        val name = contentType?.split(';')
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("charset=", ignoreCase = true) }
            ?.substringAfter('=')?.trim('"', ' ')
            ?: return null
        return runCatching { Charset.forName(name) }.getOrNull()
    }

    private companion object {
        const val TIMEOUT_MS = 10_000
    }
}