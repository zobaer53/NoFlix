

package com.zobaer53.zedmovies.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.core.ui.common.MediaTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTabPager(
    moviesTabContent: @Composable () -> Unit,
    tvShowsTabContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    backgroundColor: Color = zedMoviesTheme.colors.primary,
    selectedContentColor: Color = zedMoviesTheme.colors.accent,
    unselectedContentColor: Color = zedMoviesTheme.colors.textPrimary
) {
    val tabs = remember { MediaTab.values() }
    val pagerState = rememberPagerState()
    val selectedTabIndex = pagerState.currentPage

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = zedMoviesTheme.colors.accent
                )
            },
            backgroundColor = backgroundColor,
            divider = { TabRowDefaults.Divider(color = zedMoviesTheme.colors.primaryVariant) }
        ) {
            tabs.forEach { tab ->
                val index = tab.ordinal
                val selected = selectedTabIndex == index

                Tab(
                    selected = selected,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = stringResource(id = tab.titleResourceId),
                            style = zedMoviesTheme.typography.medium.h4
                        )
                    },
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unselectedContentColor
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            count = tabs.size
        ) { page ->
            when (page) {
                MediaTab.Movies.ordinal -> moviesTabContent()
                MediaTab.TvShows.ordinal -> tvShowsTabContent()
            }
        }
    }
}
