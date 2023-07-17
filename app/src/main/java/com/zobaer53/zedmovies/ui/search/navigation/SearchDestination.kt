

package com.zobaer53.zedmovies.ui.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.ui.search.SearchRoute

object SearchDestination : zedMoviesNavigationDestination {
    override val route = "search_route"
    override val destination = "search_destination"
}

fun NavGraphBuilder.searchGraph(
    onNavigateToListDestination: (MediaType.Common) -> Unit,
    onNavigateToDetailsDestination: (MediaType.Details) -> Unit
) = composable(route = SearchDestination.route) {
    SearchRoute(
        onSeeAllClick = onNavigateToListDestination,
        onMovieClick = { onNavigateToDetailsDestination(MediaType.Details.Movie(it)) },
        onTvShowClick = { onNavigateToDetailsDestination(MediaType.Details.TvShow(it)) }
    )
}
