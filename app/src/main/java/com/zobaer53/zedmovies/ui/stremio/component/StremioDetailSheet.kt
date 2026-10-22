package com.zobaer53.zedmovies.ui.stremio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaDetails
import com.zobaer53.zedmovies.data.stremio.model.CinemetaVideo
import com.zobaer53.zedmovies.data.stremio.model.ParsedStream
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StremioDetailView(
    meta: CinemetaMetaDetails,
    selectedSeason: Int?,
    selectedEpisode: CinemetaVideo?,
    onSeasonSelected: (Int) -> Unit,
    onEpisodeSelected: (CinemetaVideo) -> Unit,
    isLoadingStreams: Boolean,
    streams: List<ParsedStream>,
    streamErrors: List<String>,
    qualityFilter: String,
    onQualityFilterSelected: (String) -> Unit,
    onStreamAction: (ParsedStream) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onBackClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "← Back to search", color = zedMoviesTheme.colors.textPrimary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Header: Poster + Title + Description
        Row(modifier = Modifier.fillMaxWidth()) {
            if (!meta.poster.isNullOrBlank()) {
                AsyncImage(
                    model = meta.poster,
                    contentDescription = meta.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(145.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(zedMoviesTheme.colors.primaryVariant)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meta.name,
                    color = zedMoviesTheme.colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!meta.year.isNullOrBlank()) {
                    Text(
                        text = "${meta.year} • ${meta.type.replaceFirstChar { it.uppercase() }}",
                        color = zedMoviesTheme.colors.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (!meta.description.isNullOrBlank()) {
                    Text(
                        text = meta.description,
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 12.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Series Episode Picker
        if (meta.type == "series" && meta.videos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            val seasons = meta.videos.map { it.season }.filter { it > 0 }.distinct().sorted()
            val episodesInSeason = meta.videos.filter { it.season == selectedSeason }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Season Dropdown
                var seasonExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = seasonExpanded,
                    onExpandedChange = { seasonExpanded = it },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = "Season ${selectedSeason ?: 1}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = zedMoviesTheme.colors.textPrimary,
                            unfocusedTextColor = zedMoviesTheme.colors.textPrimary,
                            focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                            unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = seasonExpanded,
                        onDismissRequest = { seasonExpanded = false }
                    ) {
                        seasons.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("Season $s") },
                                onClick = {
                                    onSeasonSelected(s)
                                    seasonExpanded = false
                                }
                            )
                        }
                    }
                }

                // Episode Dropdown
                var epExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = epExpanded,
                    onExpandedChange = { epExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedEpisode?.let { "E${it.episode} • ${it.name ?: it.title ?: "Episode ${it.episode}"}" } ?: "Select episode",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = epExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = zedMoviesTheme.colors.textPrimary,
                            unfocusedTextColor = zedMoviesTheme.colors.textPrimary,
                            focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                            unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = epExpanded,
                        onDismissRequest = { epExpanded = false }
                    ) {
                        episodesInSeason.forEach { ep ->
                            DropdownMenuItem(
                                text = { Text("E${ep.episode} • ${ep.name ?: ep.title ?: "Episode ${ep.episode}"}") },
                                onClick = {
                                    onEpisodeSelected(ep)
                                    epExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Streams Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available Streams",
                color = zedMoviesTheme.colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (isLoadingStreams) {
                CircularProgressIndicator(
                    modifier = Modifier.width(20.dp).height(20.dp),
                    color = zedMoviesTheme.colors.accent,
                    strokeWidth = 2.dp
                )
            }
        }

        // Quality Filter Chips Row
        if (streams.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            val qualityCounts = streams.groupBy { it.quality.label }
            val allFilterLabels = listOf("All") + qualityCounts.keys.sorted()

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allFilterLabels) { filter ->
                    val isSelected = filter == qualityFilter
                    val count = if (filter == "All") streams.size else qualityCounts[filter]?.size ?: 0

                    Row(
                        modifier = Modifier
                            .background(
                                if (isSelected) zedMoviesTheme.colors.accent else zedMoviesTheme.colors.primaryVariant,
                                RoundedCornerShape(999.dp)
                            )
                            .clickable { onQualityFilterSelected(filter) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.Black else zedMoviesTheme.colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($count)",
                            color = if (isSelected) Color.DarkGray else zedMoviesTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Error notifications from addons
        if (streamErrors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            streamErrors.take(2).forEach { err ->
                Text(
                    text = err,
                    color = zedMoviesTheme.colors.secondaryVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Streams List
        val filteredStreams = if (qualityFilter == "All") {
            streams
        } else {
            streams.filter { it.quality.label == qualityFilter }
        }

        if (isLoadingStreams && streams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Asking addons for streams...",
                    color = zedMoviesTheme.colors.textSecondary,
                    fontSize = 13.sp
                )
            }
        } else if (filteredStreams.isEmpty() && !isLoadingStreams) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No streams found for this selection.",
                    color = zedMoviesTheme.colors.textSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredStreams.forEach { stream ->
                    StreamItemCard(
                        stream = stream,
                        onActionClick = { onStreamAction(stream) }
                    )
                }
            }
        }
    }
}
