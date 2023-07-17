

package com.zobaer53.zedmovies.ui.list.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.ui.list.ListRoute

object ListDestination : zedMoviesNavigationDestination {
    override val route = "list_route"
    override val destination = "list_destination"

    const val mediaTypeArgument = "mediaType"
    val routeWithArgument = "$route/{$mediaTypeArgument}"

    fun createNavigationRoute(mediaType: MediaType.Common) = "$route/${mediaType.mediaType}"

    fun fromSavedStateHandle(savedStateHandle: SavedStateHandle) = MediaType.Common[
        checkNotNull(savedStateHandle[mediaTypeArgument]) { MediaTypeNullMessage }
    ]
}

fun NavGraphBuilder.listGraph(
    onBackButtonClick: () -> Unit,
    onNavigateToDetailsDestination: (MediaType.Details) -> Unit
) = composable(
    route = ListDestination.routeWithArgument,
    arguments = listOf(
        navArgument(ListDestination.mediaTypeArgument) { type = NavType.StringType }
    )
) {
    ListRoute(
        onBackButtonClick = onBackButtonClick,
        onMovieClick = { onNavigateToDetailsDestination(MediaType.Details.Movie(it)) },
        onTvShowClick = { onNavigateToDetailsDestination(MediaType.Details.TvShow(it)) }
    )
}

private const val MediaTypeNullMessage = "Media type is null."
