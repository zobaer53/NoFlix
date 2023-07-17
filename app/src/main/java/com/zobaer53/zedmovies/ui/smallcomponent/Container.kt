

package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesCenteredBox
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesCircularProgressIndicator
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesMessage
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.TvShow
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asUserMessage
import com.zobaer53.zedmovies.ui.smallcomponent.util.error
import com.zobaer53.zedmovies.ui.smallcomponent.util.isEmpty
import com.zobaer53.zedmovies.ui.smallcomponent.util.isError
import com.zobaer53.zedmovies.ui.smallcomponent.util.isFinished
import com.zobaer53.zedmovies.ui.smallcomponent.util.isLoading
import com.zobaer53.zedmovies.ui.smallcomponent.util.isNotEmpty
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesSwipeRefresh

@Composable
fun MoviesAndTvShowsContainer(
    @StringRes titleResourceId: Int,
    onSeeAllClick: () -> Unit,
    movies: List<Movie>,
    tvShows: List<TvShow>,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ContainerTitleWithButton(titleResourceId = titleResourceId, onSeeAllClick = onSeeAllClick)
        Text(
            modifier = Modifier.padding(horizontal = zedMoviesTheme.spacing.extraMedium),
            text = stringResource(id = R.string.movies),
            style = zedMoviesTheme.typography.semiBold.h5,
            color = zedMoviesTheme.colors.textPrimaryVariant
        )
        Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.small))
        MoviesContainer(movies = movies, onClick = onMovieClick)
        Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.smallMedium))
        Text(
            modifier = Modifier.padding(horizontal = zedMoviesTheme.spacing.extraMedium),
            text = stringResource(id = R.string.tv_shows),
            style = zedMoviesTheme.typography.semiBold.h5,
            color = zedMoviesTheme.colors.textPrimaryVariant
        )
        Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.small))
        TvShowsContainer(tvShows = tvShows, onClick = onTvShowClick)
    }
}

@Composable
fun MoviesContainer(
    @StringRes titleResourceId: Int,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        ContainerTitleWithButton(titleResourceId = titleResourceId, onSeeAllClick = onSeeAllClick)
        Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.extraSmall))
        content()
    }
}

@Composable
fun MoviesContainer(
    movies: LazyPagingItems<Movie>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = movies.loadState.refresh.isLoading,
    emptyContent: @Composable LazyItemScope.() -> Unit = {
        zedMoviesMessage(
            modifier = Modifier.fillParentMaxSize(),
            messageResourceId = R.string.no_movie_results,
            imageResourceId = R.drawable.no_search_results
        )
    },
    loadingContent: @Composable LazyItemScope.() -> Unit = { zedMoviesCircularProgressIndicator() },
    errorContent: @Composable LazyItemScope.(errorMessage: UserMessage) -> Unit = { errorMessage ->
        zedMoviesError(
            modifier = Modifier.fillMaxWidth(),
            errorMessage = errorMessage,
            onRetry = movies::retry
        )
    }
) {
    zedMoviesSwipeRefresh(
        modifier = modifier,
        isRefreshing = isLoading,
        onRefresh = movies::refresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium),
            contentPadding = PaddingValues(zedMoviesTheme.spacing.extraMedium)
        ) {
            when {

                movies.isNotEmpty() -> {
                    items(
                        count = movies.itemCount,
                        key = movies.itemKey(),
                        contentType = movies.itemContentType(
                        )
                    ) { index ->
                        val item = movies[index]
                        if (item == null) {
                            VerticalMovieItemPlaceholder()
                        } else {
                            VerticalMovieItem(movie = item, onClick = onClick)
                        }
                    }
                }
                movies.loadState.refresh.isLoading -> {
                    items(PlaceholderCount) {
                        VerticalMovieItemPlaceholder() }
                }
                movies.loadState.refresh.isFinished -> {
                    if (movies.isEmpty()) {
                        item(content = emptyContent)
                    }
                }
                movies.loadState.refresh.isError -> {
                    item {
                        zedMoviesCenteredBox(modifier = Modifier.fillParentMaxSize()) {
                            errorContent(
                                errorMessage = movies.loadState.refresh.error.asUserMessage()
                            )
                        }
                    }
                }
            }
            if (movies.loadState.append.isLoading) {
                item { zedMoviesCenteredBox(modifier = Modifier.fillMaxWidth()) {
                    loadingContent() } }
            }
            if (movies.loadState.append.isError) {
                item {
                    errorContent(errorMessage = movies.loadState.append.error.asUserMessage()) }
            }
        }
    }
}

@Composable
fun TvShowsContainer(
    tvShows: LazyPagingItems<TvShow>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = tvShows.loadState.refresh.isLoading,
    emptyContent: @Composable LazyItemScope.() -> Unit = {
        zedMoviesMessage(
            modifier = Modifier.fillParentMaxSize(),
            messageResourceId = R.string.no_tv_show_results,
            imageResourceId = R.drawable.no_search_results
        )
    },
    loadingContent: @Composable LazyItemScope.() -> Unit = { zedMoviesCircularProgressIndicator() },
    errorContent: @Composable LazyItemScope.(errorMessage: UserMessage) -> Unit = { errorMessage ->
        zedMoviesError(
            modifier = Modifier.fillMaxWidth(),
            errorMessage = errorMessage,
            onRetry = tvShows::retry
        )
    }
) {
    zedMoviesSwipeRefresh(
        modifier = modifier,
        isRefreshing = isLoading,
        onRefresh = tvShows::refresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium),
            contentPadding = PaddingValues(zedMoviesTheme.spacing.extraMedium)
        ) {
            when {
                tvShows.isNotEmpty() -> {
                    items(
                        count = tvShows.itemCount,
                        key = tvShows.itemKey(),
                        contentType = tvShows.itemContentType(
                        )
                    ) { index ->
                        val item = tvShows[index]
                        if (item == null) {
                            VerticalTvShowItemPlaceholder()
                        } else {
                            VerticalTvShowItem(tvShow = item, onClick = onClick)
                        }
                    }
                }
                tvShows.loadState.refresh.isLoading -> {
                    items(PlaceholderCount) {
                        VerticalTvShowItemPlaceholder() }
                }
                tvShows.loadState.refresh.isFinished -> {
                    if (tvShows.isEmpty()) {
                        item(content = emptyContent)
                    }
                }
                tvShows.loadState.refresh.isError -> {
                    item {
                        zedMoviesCenteredBox(modifier = Modifier.fillParentMaxSize()) {
                            errorContent(
                                errorMessage = tvShows.loadState.refresh.error.asUserMessage()
                            )
                        }
                    }
                }
            }
            if (tvShows.loadState.append.isLoading) {
                item { zedMoviesCenteredBox(modifier = Modifier.fillMaxWidth()) {
                    loadingContent() } }
            }
            if (tvShows.loadState.append.isError) {
                item {
                    errorContent(errorMessage = tvShows.loadState.append.error.asUserMessage()) }
            }
        }
    }
}

@Composable
private fun MoviesContainer(
    movies: List<Movie>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerContent(
        modifier = modifier,
        items = movies,
        itemContent = { movie -> HorizontalMovieItem(movie = movie, onClick = onClick) },
        placeholderContent = {
            HorizontalMovieItemPlaceholder() }
    )
}

@Composable
private fun TvShowsContainer(
    tvShows: List<TvShow>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerContent(
        modifier = modifier,
        items = tvShows,
        itemContent = { tvShow ->
            HorizontalTvShowItem(tvShow = tvShow, onClick = onClick) },
        placeholderContent = {
            HorizontalTvShowItemPlaceholder() }
    )
}

@Composable
private fun <T> ContainerContent(
    items: List<T>,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
    placeholderContent: @Composable LazyItemScope.(Int) -> Unit,
    modifier: Modifier = Modifier,
    shouldShowPlaceholder: Boolean = items.isEmpty()
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.smallMedium),
        contentPadding = PaddingValues(horizontal = zedMoviesTheme.spacing.smallMedium)
    ) {
        if (shouldShowPlaceholder) {
            items(count = PlaceholderCount, itemContent = placeholderContent)
        } else {
            items(items = items, itemContent = itemContent)
        }
    }
}

@Composable
private fun ContainerTitleWithButton(
    @StringRes titleResourceId: Int,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = zedMoviesTheme.spacing.extraMedium)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = titleResourceId),
            style = zedMoviesTheme.typography.semiBold.h4,
            color = zedMoviesTheme.colors.textPrimary
        )
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = stringResource(id = R.string.see_all),
                style = zedMoviesTheme.typography.medium.h5,
                color = zedMoviesTheme.colors.accent
            )
        }
    }
}

private const val PlaceholderCount = 20
