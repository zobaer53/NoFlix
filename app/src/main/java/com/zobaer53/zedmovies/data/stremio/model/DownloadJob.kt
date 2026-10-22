package com.zobaer53.zedmovies.data.stremio.model

enum class DownloadStatus {
    STARTING,
    DOWNLOADING,
    RETRYING,
    COMPLETED,
    CANCELLED,
    ERROR
}

data class DownloadJob(
    val id: Long,
    val title: String,
    val kind: StreamKind,
    val quality: String? = null,
    val status: DownloadStatus = DownloadStatus.STARTING,
    val localFilePath: String? = null,
    val bytesReceived: Long = 0,
    val totalBytes: Long? = null,
    val speedBytesPerSec: Long = 0,
    val errorMessage: String? = null,
    val retryAttempt: Int = 0,
    val startedAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() {
            val total = totalBytes ?: return 0f
            if (total <= 0) return 0f
            return (bytesReceived.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        }

    val progressPercent: String
        get() = "${(progress * 100).toInt()}%"

    val isFinished: Boolean
        get() = status == DownloadStatus.COMPLETED ||
            status == DownloadStatus.CANCELLED ||
            status == DownloadStatus.ERROR
}

