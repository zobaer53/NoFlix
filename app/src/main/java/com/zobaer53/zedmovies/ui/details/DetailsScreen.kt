

package com.zobaer53.zedmovies.ui.details

import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesSwipeRefresh
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesTopAppBar
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesBackButton
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesCenteredError
import com.zobaer53.zedmovies.ui.smallcomponent.SnackbarUserMessageHandler
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asUserMessage
import com.zobaer53.zedmovies.ui.details.components.MovieDetailsItem
import com.zobaer53.zedmovies.ui.details.components.MovieDetailsItemPlaceholder
import com.zobaer53.zedmovies.ui.details.components.TvShowDetailsItem
import com.zobaer53.zedmovies.ui.details.components.TvShowDetailsItemPlaceholder

@Composable
internal fun DetailsRoute(
    onBackButtonClick: () -> Unit,
    onShowMessage: (String) -> Unit,
    onSetSystemBarsColorTransparent: () -> Unit,
    onResetSystemBarsColor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailsScreen(
        uiState = uiState,
        onShowMessage = onShowMessage,
        onRefresh = { viewModel.onEvent(DetailsEvent.Refresh) },
        onBackButtonClick = onBackButtonClick,
        onWishlistMovieClick = { viewModel.onEvent(DetailsEvent.WishlistMovie) },
        onWishlistTvShowClick = { viewModel.onEvent(DetailsEvent.WishlistTvShow) },
        onRetry = { viewModel.onEvent(DetailsEvent.Retry) },
        onOfflineModeClick = { viewModel.onEvent(DetailsEvent.ClearError) },
        onUserMessageDismiss = { viewModel.onEvent(DetailsEvent.ClearUserMessage) },
        modifier = modifier
    )

    DisposableEffect(onSetSystemBarsColorTransparent, onResetSystemBarsColor) {
        onSetSystemBarsColorTransparent()
        onDispose { onResetSystemBarsColor() }
    }
}

@Composable
private fun DetailsScreen(
    uiState: DetailsUiState,
    onShowMessage: (String) -> Unit,
    onRefresh: () -> Unit,
    onBackButtonClick: () -> Unit,
    onWishlistMovieClick: () -> Unit,
    onWishlistTvShowClick: () -> Unit,
    onRetry: () -> Unit,
    onOfflineModeClick: () -> Unit,
    onUserMessageDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SnackbarUserMessageHandler(
        userMessage = uiState.userMessage,
        onShowMessage = onShowMessage,
        onDismiss = onUserMessageDismiss
    )
    zedMoviesSwipeRefresh(
        modifier = modifier,
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        indicatorPadding = WindowInsets.safeDrawing.asPaddingValues()
    ) {
        if (uiState.error != null) {
         /*   ErrorContent(
                errorMessage = uiState.error.asUserMessage(),
                isOfflineModeAvailable = uiState.isOfflineModeAvailable,
                onBackButtonClick = onBackButtonClick,
                onRetry = onRetry,
                onOfflineModeClick = onOfflineModeClick
            )*/
            Log.i("error1","ui state error msg ${uiState.error?.message} " +
                    " asUserMessage ${uiState.error?.asUserMessage()} " +
                    " localizedMessage ${uiState.error?.cause}")

       }
        //else {
            DetailsContent(
                uiState = uiState,
                onBackButtonClick = onBackButtonClick,
                onWishlistMovieClick = onWishlistMovieClick,
                onWishlistTvShowClick = onWishlistTvShowClick
            )
      //  }
    }
}

@Composable
private fun DetailsContent(
    uiState: DetailsUiState,
    onBackButtonClick: () -> Unit,
    onWishlistMovieClick: () -> Unit,
    onWishlistTvShowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        when (uiState.mediaType) {
            is MediaType.Details.Movie -> {
                if (uiState.movie == null) {
                    MovieDetailsItemPlaceholder(
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistMovieClick
                    )
                } else {
                    MovieDetailsItem(
                        movieDetails = uiState.movie,
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistMovieClick
                    )
                }
            }

            is MediaType.Details.TvShow -> {
                if (uiState.tvShow == null) {
                    TvShowDetailsItemPlaceholder(
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistMovieClick
                    )
                } else {
                    TvShowDetailsItem(
                        tvShowDetails = uiState.tvShow,
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistTvShowClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ErrorContent(
    errorMessage: UserMessage,
    isOfflineModeAvailable: Boolean,
    onBackButtonClick: () -> Unit,
    onRetry: () -> Unit,
    onOfflineModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            zedMoviesTopAppBar(
                title = {},
                navigationIcon = { zedMoviesBackButton(onClick = onBackButtonClick) }
            )
        }
    ) { innerPadding ->
        zedMoviesCenteredError(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            errorMessage = errorMessage,
            onRetry = onRetry,
            shouldShowOfflineMode = isOfflineModeAvailable,
            onOfflineModeClick = onOfflineModeClick
        )
    }
}
