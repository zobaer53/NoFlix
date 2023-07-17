
package com.zobaer53.zedmovies.ui.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.ui.home.HomeRoute

object HomeDestination : zedMoviesNavigationDestination {
    override val route = "home_route"
    override val destination = "home_destination"
}

fun NavGraphBuilder.homeGraph(
    onNavigateToListDestination: (MediaType.Common) -> Unit,
    onNavigateToDetailsDestination: (MediaType.Details) -> Unit
) = composable(route = HomeDestination.route) {
    HomeRoute(
        onSeeAllClick = onNavigateToListDestination,
        onMovieClick = { onNavigateToDetailsDestination(MediaType.Details.Movie(it)) },
        onTvShowClick = { onNavigateToDetailsDestination(MediaType.Details.TvShow(it)) }
    )
}
