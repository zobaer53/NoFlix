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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.data.stremio.model.ParsedStream
import com.zobaer53.zedmovies.data.stremio.model.StreamKind
import com.zobaer53.zedmovies.data.stremio.model.StreamQuality
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun StreamItemCard(
    stream: ParsedStream,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = zedMoviesTheme.colors.primaryVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QualityBadge(quality = stream.quality)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stream.displayText,
                    color = zedMoviesTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stream.addonName,
                        color = zedMoviesTheme.colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "•",
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )

                    Text(
                        text = stream.kind.label,
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )

                    if (stream.formattedSize.isNotBlank()) {
                        Text(
                            text = "•",
                            color = zedMoviesTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = stream.formattedSize,
                            color = zedMoviesTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (stream.seeders != null) {
                        Text(
                            text = "• 👤 ${stream.seeders}",
                            color = zedMoviesTheme.colors.secondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            val actionLabel = when (stream.kind) {
                StreamKind.TORRENT -> "Magnet"
                StreamKind.HLS -> "Select"
                StreamKind.HTTP -> "Download"
                StreamKind.YOUTUBE -> "YouTube"
            }

            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = zedMoviesTheme.colors.accent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QualityBadge(
    quality: StreamQuality,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (quality) {
        StreamQuality.Q_4K -> Color(0xFF6B3CE8) to Color.White
        StreamQuality.Q_1440P -> Color(0xFF2C59D9) to Color.White
        StreamQuality.Q_1080P -> Color(0xFF1B77B8) to Color.White
        StreamQuality.Q_720P -> Color(0xFF15836C) to Color.White
        StreamQuality.Q_576P,
        StreamQuality.Q_480P,
        StreamQuality.Q_360P -> Color(0xFF383B47) to Color(0xFFE0E0E0)
        StreamQuality.UNKNOWN -> Color(0xFF2E313D) to Color(0xFFB0B0B0)
    }

    Text(
        text = quality.label,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

