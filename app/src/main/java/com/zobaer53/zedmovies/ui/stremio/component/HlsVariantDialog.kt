package com.zobaer53.zedmovies.ui.stremio.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.data.stremio.model.HlsVariant
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import java.util.Locale

@Composable
fun HlsVariantDialog(
    variants: List<HlsVariant>,
    isLoading: Boolean,
    onVariantSelected: (HlsVariant) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pick HLS Quality",
                color = zedMoviesTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = zedMoviesTheme.colors.accent,
                            modifier = Modifier.height(32.dp).width(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Reading playlist variants...",
                            color = zedMoviesTheme.colors.textSecondary
                        )
                    }
                } else if (variants.isEmpty()) {
                    Text(
                        text = "No quality variants found for this stream.",
                        color = zedMoviesTheme.colors.textSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(variants) { variant ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onVariantSelected(variant) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = zedMoviesTheme.colors.primaryVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = variant.quality,
                                            color = zedMoviesTheme.colors.textPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (variant.resolution != null) {
                                            Text(
                                                text = variant.resolution,
                                                color = zedMoviesTheme.colors.textSecondary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    if (variant.bandwidth != null && variant.bandwidth > 0) {
                                        val mbps = variant.bandwidth.toDouble() / 1_000_000.0
                                        Text(
                                            text = String.format(Locale.US, "%.1f Mbps", mbps),
                                            color = zedMoviesTheme.colors.accent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = zedMoviesTheme.colors.textSecondary)
            }
        },
        containerColor = zedMoviesTheme.colors.primary
    )
}

