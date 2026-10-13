

package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.lerp
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.ui.smallcomponent.common.MediaTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedTabIndex = pagerState.currentPage

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorLayout { measurable, constraints, tabPositions ->
                        measurePagerTabIndicator(measurable, constraints, tabPositions, pagerState)
                    },
                    color = zedMoviesTheme.colors.accent
                )
            },
            containerColor = backgroundColor,
            divider = { HorizontalDivider(color = zedMoviesTheme.colors.primaryVariant) }
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
            state = pagerState
        ) { page ->
            when (page) {
                MediaTab.Movies.ordinal -> moviesTabContent()
                MediaTab.TvShows.ordinal -> tvShowsTabContent()
            }
        }
    }
}

/** Places the tab indicator at the pager's scroll position, including mid-swipe. */
private fun MeasureScope.measurePagerTabIndicator(
    measurable: Measurable,
    constraints: Constraints,
    tabPositions: List<TabPosition>,
    pagerState: PagerState
): MeasureResult =
    if (tabPositions.isEmpty()) {
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pagerState.currentPage)
        val currentTab = tabPositions[currentPage]
        val fraction = pagerState.currentPageOffsetFraction
        val targetTab = when {
            fraction > 0 -> tabPositions.getOrNull(currentPage + 1)
            fraction < 0 -> tabPositions.getOrNull(currentPage - 1)
            else -> null
        } ?: currentTab
        val progress = kotlin.math.abs(fraction)
        val indicatorWidth = lerp(currentTab.width, targetTab.width, progress).roundToPx()
        val indicatorOffset = lerp(currentTab.left, targetTab.left, progress).roundToPx()
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
