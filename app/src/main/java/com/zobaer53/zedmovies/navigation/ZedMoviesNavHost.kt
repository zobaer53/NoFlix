
package com.zobaer53.zedmovies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.zobaer53.zedmovies.ui.details.navigation.DetailsDestination
import com.zobaer53.zedmovies.ui.details.navigation.detailsGraph
import com.zobaer53.zedmovies.ui.home.navigation.homeGraph
import com.zobaer53.zedmovies.ui.list.navigation.ListDestination
import com.zobaer53.zedmovies.ui.list.navigation.listGraph
import com.zobaer53.zedmovies.ui.search.navigation.searchGraph
import com.zobaer53.zedmovies.ui.wishlist.navigation.wishlistGraph

@Composable
fun ZedMoviesNavHost(
    navController: NavHostController,
    startDestination: zedMoviesNavigationDestination,
    onNavigateToDestination: (zedMoviesNavigationDestination, String) -> Unit,
    onBackClick: () -> Unit,
    onShowMessage: (String) -> Unit,
    onSetSystemBarsColorTransparent: () -> Unit,
    onResetSystemBarsColor: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ) {
        homeGraph(
            onNavigateToListDestination = {
                onNavigateToDestination(ListDestination, ListDestination.createNavigationRoute(it))
            },
            onNavigateToDetailsDestination = {
                onNavigateToDestination(
                    DetailsDestination,
                    DetailsDestination.createNavigationRoute(it)
                )
            }
        )
        searchGraph(
            onNavigateToListDestination = {
                onNavigateToDestination(ListDestination, ListDestination.createNavigationRoute(it))
            },
            onNavigateToDetailsDestination = {
                onNavigateToDestination(
                    DetailsDestination,
                    DetailsDestination.createNavigationRoute(it)
                )
            }
        )
        wishlistGraph(
            onNavigateToDetailsDestination = {
                onNavigateToDestination(
                    DetailsDestination,
                    DetailsDestination.createNavigationRoute(it)
                )
            }
        )
        listGraph(
            onBackButtonClick = onBackClick,
            onNavigateToDetailsDestination = {
                onNavigateToDestination(
                    DetailsDestination,
                    DetailsDestination.createNavigationRoute(it)
                )
            }
        )
        detailsGraph(
            onBackButtonClick = onBackClick,
            onShowMessage = onShowMessage,
            onSetSystemBarsColorTransparent = onSetSystemBarsColorTransparent,
            onResetSystemBarsColor = onResetSystemBarsColor
        )
    }
}
