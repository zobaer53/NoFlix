package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZedMoviesSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = zedMoviesTheme.colors.primaryVariant,
    contentColor: Color = zedMoviesTheme.colors.accent,
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    indicatorAlignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .padding(indicatorPadding)
                    .align(indicatorAlignment),
                containerColor = backgroundColor,
                color = contentColor
            )
        }
    ) {
        content()
    }
}
