package com.zobaer53.zedmovies.ui.stremio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.data.stremio.model.DownloadJob
import com.zobaer53.zedmovies.data.stremio.model.DownloadStatus
import com.zobaer53.zedmovies.data.stremio.parser.SizeParser
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun DownloadItemCard(
    job: DownloadJob,
    onOpenClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = zedMoviesTheme.colors.primaryVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!job.quality.isNullOrBlank()) {
                    Text(
                        text = job.quality,
                        modifier = Modifier
                            .background(zedMoviesTheme.colors.accentContainer, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        color = zedMoviesTheme.colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = job.title,
                    modifier = Modifier.weight(1f),
                    color = zedMoviesTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                DownloadStatusBadge(status = job.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            val progressColor = when (job.status) {
                DownloadStatus.COMPLETED -> zedMoviesTheme.colors.secondary
                DownloadStatus.ERROR -> zedMoviesTheme.colors.error
                DownloadStatus.CANCELLED -> zedMoviesTheme.colors.textSecondary
                else -> zedMoviesTheme.colors.accent
            }

            LinearProgressIndicator(
                progress = {
                    if (job.status == DownloadStatus.COMPLETED) 1f else job.progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = progressColor,
                trackColor = Color(0xFF1E212D),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statsText = buildString {
                    append(SizeParser.format(job.bytesReceived))
                    if (job.totalBytes != null && job.totalBytes > 0) {
                        append(" / ")
                        append(SizeParser.format(job.totalBytes))
                    }
                    if (job.speedBytesPerSec > 0 && job.status == DownloadStatus.DOWNLOADING) {
                        append(" • ")
                        append(SizeParser.formatSpeed(job.speedBytesPerSec))
                    }
                    if (job.retryAttempt > 0 && job.status == DownloadStatus.RETRYING) {
                        append(" • Retry #${job.retryAttempt}")
                    }
                    if (!job.errorMessage.isNullOrBlank()) {
                        append(" • ")
                        append(job.errorMessage)
                    }
                }

                Text(
                    text = statsText,
                    color = if (job.status == DownloadStatus.ERROR) zedMoviesTheme.colors.error else zedMoviesTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (job.totalBytes != null && job.totalBytes > 0) {
                    Text(
                        text = job.progressPercent,
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (job.status == DownloadStatus.COMPLETED && job.localFilePath != null) {
                    Button(
                        onClick = onOpenClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = zedMoviesTheme.colors.secondary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Open / Play", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (!job.isFinished) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Cancel", color = zedMoviesTheme.colors.textSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "Delete", color = zedMoviesTheme.colors.error, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DownloadStatusBadge(
    status: DownloadStatus,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        DownloadStatus.STARTING -> "Starting" to Color(0xFFFFB020)
        DownloadStatus.DOWNLOADING -> "Downloading" to Color(0xFF12CDD9)
        DownloadStatus.RETRYING -> "Retrying" to Color(0xFFFFB020)
        DownloadStatus.COMPLETED -> "Done" to Color(0xFF22B07D)
        DownloadStatus.CANCELLED -> "Cancelled" to Color(0xFF92929D)
        DownloadStatus.ERROR -> "Error" to Color(0xFFFB4141)
    }

    Text(
        text = label,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

