
package com.zobaer53.zedmovies.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesSnackbarHost
import com.zobaer53.zedmovies.ui.designsystem.component.LocalSnackbarHostState
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.navigation.ZedMoviesNavHost
import com.zobaer53.zedmovies.ui.component.zedMoviesBottomBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Suppress("ModifierMissing")
@Composable
fun zedMoviesApp(appState: zedMoviesAppState = rememberzedMoviesAppState()) {
    zedMoviesTheme {
        val systemBarsColor = zedMoviesTheme.colors.primary
        LaunchedEffect(systemBarsColor) { appState.setSystemBarsColor(systemBarsColor) }

        CompositionLocalProvider(
            LocalSnackbarHostState provides appState.snackbarHostState
        ) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = appState.shouldShowBottomBar,
                        enter = BottomBarEnterTransition,
                        exit = BottomBarExitTransition
                    ) {
                        zedMoviesBottomBar(
                            destinations = appState.topLevelDestinations,
                            currentDestination = appState.currentTopLevelDestination,
                            onNavigateToDestination = appState::navigate
                        )
                    }
                },
                snackbarHost = {
                    zedMoviesSnackbarHost(
                        modifier = Modifier.windowInsetsPadding(
                            if (appState.shouldShowBottomBar) {
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                            } else {
                                WindowInsets.safeDrawing
                            }
                        ),
                        snackbarHostState = appState.snackbarHostState
                    )
                },
                contentWindowInsets = WindowInsets(
                    left = 0.dp,
                    top = 0.dp,
                    right = 0.dp,
                    bottom = 0.dp
                )
            ) { innerPadding ->
                ZedMoviesNavHost(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .consumeWindowInsets(paddingValues = innerPadding),
                    navController = appState.navController,
                    startDestination = appState.startDestination,
                    onNavigateToDestination = appState::navigate,
                    onBackClick = appState::onBackClick,
                    onShowMessage = { message -> appState.showMessage(message) },
                    onSetSystemBarsColorTransparent = { appState.setSystemBarsColor(Color.Transparent) },
                    onResetSystemBarsColor = { appState.setSystemBarsColor(systemBarsColor) }
                )
            }
        }
    }
}

private val BottomBarEnterTransition = fadeIn() + expandVertically(expandFrom = Alignment.Top)
private val BottomBarExitTransition = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
