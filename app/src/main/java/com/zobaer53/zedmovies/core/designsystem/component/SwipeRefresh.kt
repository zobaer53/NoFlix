

package com.zobaer53.zedmovies.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun zedMoviesSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = zedMoviesTheme.colors.primaryVariant,
    contentColor: Color = zedMoviesTheme.colors.accent,
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    indicatorAlignment: Alignment = Alignment.TopCenter,
    scale: Boolean = true,
    content: @Composable () -> Unit
) {
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)
    Box(modifier = modifier.pullRefresh(state = state)) {
        content()

        PullRefreshIndicator(
            modifier = Modifier
                .padding(indicatorPadding)
                .align(indicatorAlignment),
            refreshing = isRefreshing,
            state = state,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            scale = scale
        )
    }
}
