package com.zobaer53.zedmovies.ui.stremio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.zobaer53.zedmovies.data.stremio.model.CinemetaMetaSummary
import com.zobaer53.zedmovies.data.stremio.model.InspectResult
import com.zobaer53.zedmovies.data.stremio.parser.SizeParser
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.ui.stremio.component.DownloadItemCard
import com.zobaer53.zedmovies.ui.stremio.component.HlsVariantDialog
import com.zobaer53.zedmovies.ui.stremio.component.StremioDetailView
import com.zobaer53.zedmovies.ui.stremio.component.StremioSearchBar

@Composable
fun StremioScreen(
    onBackClick: (() -> Unit)? = null,
    viewModel: StremioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val addons by viewModel.addons.collectAsState()
    val downloadDir by viewModel.downloadDir.collectAsState()

    val activeDownloadCount = downloads.count { !it.isFinished }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(zedMoviesTheme.colors.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stremio Downloader",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = zedMoviesTheme.colors.textPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(zedMoviesTheme.colors.secondary, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Addons ready",
                            fontSize = 12.sp,
                            color = zedMoviesTheme.colors.textSecondary
                        )
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = state.currentTab.ordinal,
                    containerColor = zedMoviesTheme.colors.primary,
                    contentColor = zedMoviesTheme.colors.accent,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[state.currentTab.ordinal]),
                            color = zedMoviesTheme.colors.accent
                        )
                    }
                ) {
                    StremioTab.entries.forEach { tab ->
                        val isSelected = state.currentTab == tab
                        val title = if (tab == StremioTab.DOWNLOADS && activeDownloadCount > 0) {
                            "${tab.title} ($activeDownloadCount)"
                        } else {
                            tab.title
                        }

                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) zedMoviesTheme.colors.accent else zedMoviesTheme.colors.textSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = zedMoviesTheme.colors.primary
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (state.currentTab) {
                StremioTab.SEARCH -> {
                    SearchTabContent(
                        state = state,
                        viewModel = viewModel
                    )
                }
                StremioTab.PASTE_LINK -> {
                    PasteLinkTabContent(
                        state = state,
                        viewModel = viewModel
                    )
                }
                StremioTab.DOWNLOADS -> {
                    DownloadsTabContent(
                        downloads = downloads,
                        viewModel = viewModel
                    )
                }
                StremioTab.SETTINGS -> {
                    SettingsTabContent(
                        addons = addons,
                        downloadDir = downloadDir,
                        state = state,
                        viewModel = viewModel
                    )
                }
            }

            // HLS variant dialog
            if (state.hlsTargetStream != null) {
                HlsVariantDialog(
                    variants = state.hlsVariants,
                    isLoading = state.isLoadingHlsVariants,
                    onVariantSelected = { viewModel.onHlsVariantSelected(it) },
                    onDismiss = { viewModel.dismissHlsDialog() }
                )
            }
        }
    }
}

@Composable
private fun SearchTabContent(
    state: StremioUiState,
    viewModel: StremioViewModel
) {
    if (state.selectedMeta != null) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                StremioDetailView(
                    meta = state.selectedMeta,
                    selectedSeason = state.selectedSeason,
                    selectedEpisode = state.selectedEpisode,
                    onSeasonSelected = { viewModel.selectSeason(it) },
                    onEpisodeSelected = { viewModel.selectEpisode(it) },
                    isLoadingStreams = state.isLoadingStreams,
                    streams = state.streams,
                    streamErrors = state.streamErrors,
                    qualityFilter = state.qualityFilter,
                    onQualityFilterSelected = { viewModel.setQualityFilter(it) },
                    onStreamAction = { viewModel.onStreamAction(it) },
                    onBackClick = { viewModel.closeDetails() }
                )
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            StremioSearchBar(
                query = state.searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                selectedType = state.searchType,
                onTypeSelected = { viewModel.onSearchTypeChanged(it) },
                onSearch = { viewModel.search() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isSearching || state.isLoadingDetails) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = zedMoviesTheme.colors.accent)
                }
            } else if (state.searchError != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.searchError,
                        color = zedMoviesTheme.colors.error,
                        fontSize = 14.sp
                    )
                }
            } else if (state.searchResults.isEmpty() && state.searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found on Cinemeta",
                        color = zedMoviesTheme.colors.textSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.searchResults) { item ->
                        SearchPosterCard(
                            meta = item,
                            onClick = { viewModel.openMeta(item.type, item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPosterCard(
    meta: CinemetaMetaSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = zedMoviesTheme.colors.primaryVariant
        )
    ) {
        Column {
            AsyncImage(
                model = meta.poster,
                contentDescription = meta.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(Color(0xFF252836))
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = meta.name,
                    color = zedMoviesTheme.colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${meta.year ?: ""} • ${meta.type}",
                    color = zedMoviesTheme.colors.textSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PasteLinkTabContent(
    state: StremioUiState,
    viewModel: StremioViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Paste a direct video URL (.mp4, .mkv...), an HLS playlist (.m3u8), or a magnet link / info hash.",
                color = zedMoviesTheme.colors.textSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.linkInput,
                onValueChange = { viewModel.onLinkInputChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://.../video.m3u8 or magnet:?xt=urn:btih:...", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = zedMoviesTheme.colors.accent,
                    unfocusedBorderColor = Color(0xFF2F2A47),
                    focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    focusedTextColor = zedMoviesTheme.colors.textPrimary,
                    unfocusedTextColor = zedMoviesTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.linkTitle,
                onValueChange = { viewModel.onLinkTitleChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Custom file name (optional)", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = zedMoviesTheme.colors.accent,
                    unfocusedBorderColor = Color(0xFF2F2A47),
                    focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                    focusedTextColor = zedMoviesTheme.colors.textPrimary,
                    unfocusedTextColor = zedMoviesTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.inspectLink() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = zedMoviesTheme.colors.accent)
            ) {
                if (state.isInspecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Inspect Link", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state.inspectError != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.error.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.inspectError,
                        color = zedMoviesTheme.colors.error,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }

        when (val inspect = state.inspectResult) {
            is InspectResult.Torrent -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primaryVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Torrent: ${inspect.info.infoHash}",
                                color = zedMoviesTheme.colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.openMagnet(
                                        inspect.info.infoHash,
                                        state.linkTitle.ifBlank { inspect.info.name },
                                        inspect.info.sources
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = zedMoviesTheme.colors.secondary)
                            ) {
                                Text("Open in Torrent Client", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            is InspectResult.Hls -> {
                item {
                    Text("Available Qualities", color = zedMoviesTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                }
                items(inspect.variants) { variant ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primaryVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(variant.quality, color = zedMoviesTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                                if (variant.bandwidth != null) {
                                    Text(
                                        "${variant.bandwidth / 1000} kbps",
                                        color = zedMoviesTheme.colors.textSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Button(
                                onClick = { viewModel.startDownloadFromInspect(variant.url, variant.quality) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = zedMoviesTheme.colors.accent)
                            ) {
                                Text("Download", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            is InspectResult.Http -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primaryVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Direct Video Link", color = zedMoviesTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                                Text(
                                    inspect.url,
                                    color = zedMoviesTheme.colors.textSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.startDownloadFromInspect(inspect.url) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = zedMoviesTheme.colors.accent)
                            ) {
                                Text("Download", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

@Composable
private fun DownloadsTabContent(
    downloads: List<com.zobaer53.zedmovies.data.stremio.model.DownloadJob>,
    viewModel: StremioViewModel
) {
    if (downloads.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No downloads yet.\nStreams you download will appear here.",
                color = zedMoviesTheme.colors.textSecondary,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(downloads, key = { it.id }) { job ->
                DownloadItemCard(
                    job = job,
                    onOpenClick = { viewModel.openDownloadedFile(job.id) },
                    onCancelClick = { viewModel.cancelDownload(job.id) },
                    onDeleteClick = { viewModel.deleteJob(job.id, deleteFile = true) }
                )
            }
        }
    }
}

@Composable
private fun SettingsTabContent(
    addons: List<String>,
    downloadDir: String,
    state: StremioUiState,
    viewModel: StremioViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Stream Addons",
                color = zedMoviesTheme.colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Addons provide streams for movies and series (e.g. Torrentio). Manifest URLs end in /manifest.json.",
                color = zedMoviesTheme.colors.textSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.newAddonInput,
                    onValueChange = { viewModel.onNewAddonInputChanged(it) },
                    placeholder = { Text("https://torrentio.strem.fun/manifest.json", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = zedMoviesTheme.colors.accent,
                        unfocusedBorderColor = Color(0xFF2F2A47),
                        focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                        unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant,
                        focusedTextColor = zedMoviesTheme.colors.textPrimary,
                        unfocusedTextColor = zedMoviesTheme.colors.textPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.addAddon() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = zedMoviesTheme.colors.accent)
                ) {
                    if (state.isAddingAddon) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!state.settingsMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.settingsMessage,
                    color = zedMoviesTheme.colors.accent,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Text("Installed Addons", color = zedMoviesTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
        }

        items(addons) { manifestUrl ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primaryVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = manifestUrl,
                        color = zedMoviesTheme.colors.textPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(onClick = { viewModel.removeAddon(manifestUrl) }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Remove",
                            tint = zedMoviesTheme.colors.error
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Download Folder",
                color = zedMoviesTheme.colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = zedMoviesTheme.colors.primaryVariant)
            ) {
                Text(
                    text = downloadDir,
                    color = zedMoviesTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
