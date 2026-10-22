package com.zobaer53.zedmovies.data.stremio.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.zobaer53.zedmovies.data.stremio.model.DownloadJob
import com.zobaer53.zedmovies.data.stremio.model.DownloadStatus
import com.zobaer53.zedmovies.data.stremio.model.StreamKind
import com.zobaer53.zedmovies.data.stremio.parser.MagnetParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StremioDownloadManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val idCounter = AtomicLong(1)

    private val _jobsFlow = MutableStateFlow<List<DownloadJob>>(emptyList())
    val jobsFlow: StateFlow<List<DownloadJob>> = _jobsFlow.asStateFlow()

    private val activeCoroutineJobs = ConcurrentHashMap<Long, Job>()

    private fun safeName(name: String): String {
        return name.replace(Regex("[/\\\\:*?\"<>|\\x00-\\x1f]"), "_")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(180)
            .ifBlank { "video" }
    }

    private fun uniqueFile(dir: File, baseName: String, ext: String): File {
        var file = File(dir, "$baseName$ext")
        var counter = 1
        while (file.exists()) {
            file = File(dir, "$baseName ($counter)$ext")
            counter++
        }
        return file
    }

    fun startHttpDownload(
        title: String,
        url: String,
        headers: Map<String, String> = emptyMap(),
        quality: String? = null,
        targetDir: String
    ): Long {
        val jobId = idCounter.getAndIncrement()
        val job = DownloadJob(
            id = jobId,
            title = title,
            kind = StreamKind.HTTP,
            quality = quality,
            status = DownloadStatus.STARTING
        )

        _jobsFlow.update { current -> listOf(job) + current }

        val coroutineJob = scope.launch {
            runHttpDownloadLoop(jobId, title, url, headers, targetDir)
        }
        activeCoroutineJobs[jobId] = coroutineJob

        return jobId
    }

    private suspend fun runHttpDownloadLoop(
        jobId: Long,
        title: String,
        url: String,
        headers: Map<String, String>,
        targetDir: String
    ) {
        val dir = File(targetDir)
        if (!dir.exists()) dir.mkdirs()

        val cleanTitle = safeName(title)
        val ext = when {
            url.contains(".mkv", ignoreCase = true) -> ".mkv"
            url.contains(".mp4", ignoreCase = true) -> ".mp4"
            url.contains(".webm", ignoreCase = true) -> ".webm"
            else -> ".mp4"
        }

        var targetFile = uniqueFile(dir, cleanTitle, ext)
        updateJob(jobId) { it.copy(localFilePath = targetFile.absolutePath) }

        var attempts = 0
        val maxAttempts = 20

        while (attempts < maxAttempts) {
            val startByte = if (targetFile.exists()) targetFile.length() else 0L

            try {
                updateJob(jobId) {
                    it.copy(
                        status = DownloadStatus.DOWNLOADING,
                        bytesReceived = startByte
                    )
                }

                val requestBuilder = Request.Builder().url(url)
                headers.forEach { (k, v) -> requestBuilder.addHeader(k, v) }
                if (startByte > 0) {
                    requestBuilder.addHeader("Range", "bytes=$startByte-")
                }

                okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
                    if (response.code == 416) {
                        // Already finished
                        updateJob(jobId) {
                            it.copy(
                                status = DownloadStatus.COMPLETED,
                                bytesReceived = targetFile.length(),
                                totalBytes = targetFile.length(),
                                speedBytesPerSec = 0
                            )
                        }
                        return
                    }

                    if (!response.isSuccessful) {
                        throw IllegalStateException("Server returned HTTP ${response.code}")
                    }

                    val resumed = response.code == 206
                    val contentLength = response.body.contentLength()
                    val total = if (contentLength > 0) {
                        if (resumed) startByte + contentLength else contentLength
                    } else null

                    updateJob(jobId) {
                        it.copy(
                            totalBytes = total ?: it.totalBytes,
                            bytesReceived = if (resumed) startByte else 0L
                        )
                    }

                    val inputStream: InputStream = response.body.byteStream()
                    val outputStream = FileOutputStream(targetFile, resumed)

                    val buffer = ByteArray(64 * 1024)
                    var bytesRead: Int
                    var currentReceived = if (resumed) startByte else 0L
                    var lastTick = System.currentTimeMillis()
                    var lastBytes = currentReceived

                    outputStream.use { out ->
                        inputStream.use { inp ->
                            while (inp.read(buffer).also { bytesRead = it } != -1) {
                                out.write(buffer, 0, bytesRead)
                                currentReceived += bytesRead

                                val now = System.currentTimeMillis()
                                if (now - lastTick >= 800) {
                                    val speed = ((currentReceived - lastBytes) * 1000) / (now - lastTick)
                                    lastTick = now
                                    lastBytes = currentReceived

                                    updateJob(jobId) {
                                        it.copy(
                                            bytesReceived = currentReceived,
                                            speedBytesPerSec = speed
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Check if complete
                    if (total == null || currentReceived >= total) {
                        updateJob(jobId) {
                            it.copy(
                                status = DownloadStatus.COMPLETED,
                                bytesReceived = currentReceived,
                                totalBytes = currentReceived,
                                speedBytesPerSec = 0
                            )
                        }
                        return
                    } else {
                        throw IllegalStateException("Connection closed before download finished")
                    }
                }
            } catch (e: CancellationException) {
                updateJob(jobId) {
                    it.copy(status = DownloadStatus.CANCELLED, speedBytesPerSec = 0)
                }
                return
            } catch (e: Exception) {
                attempts++
                if (attempts >= maxAttempts) {
                    updateJob(jobId) {
                        it.copy(
                            status = DownloadStatus.ERROR,
                            errorMessage = e.message ?: "Download failed",
                            speedBytesPerSec = 0
                        )
                    }
                    return
                }

                updateJob(jobId) {
                    it.copy(
                        status = DownloadStatus.RETRYING,
                        retryAttempt = attempts,
                        speedBytesPerSec = 0
                    )
                }
                delay(3000)
            }
        }
    }

    fun cancelDownload(jobId: Long) {
        activeCoroutineJobs[jobId]?.cancel()
        activeCoroutineJobs.remove(jobId)
        updateJob(jobId) {
            it.copy(status = DownloadStatus.CANCELLED, speedBytesPerSec = 0)
        }
    }

    fun deleteJob(jobId: Long, deleteFile: Boolean = false) {
        cancelDownload(jobId)
        val job = _jobsFlow.value.find { it.id == jobId }
        if (deleteFile && job?.localFilePath != null) {
            try {
                File(job.localFilePath).delete()
            } catch (_: Exception) {}
        }
        _jobsFlow.update { list -> list.filter { it.id != jobId } }
    }

    fun openDownloadedFile(jobId: Long) {
        val job = _jobsFlow.value.find { it.id == jobId } ?: return
        val path = job.localFilePath ?: return
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist: $path", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = try {
                FileProvider.getUriForFile(context, authority, file)
            } catch (_: Exception) {
                Uri.fromFile(file)
            }

            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "video/*"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun openMagnetLink(infoHash: String, title: String? = null, sources: List<String> = emptyList()) {
        val magnetUri = MagnetParser.buildMagnetUri(infoHash, title, sources)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(magnetUri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // No app capable of handling magnet links
            Toast.makeText(
                context,
                "No torrent app found. Install LibreTorrent or Flud to stream torrents.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateJob(jobId: Long, transform: (DownloadJob) -> DownloadJob) {
        _jobsFlow.update { list ->
            list.map { if (it.id == jobId) transform(it) else it }
        }
    }
}
