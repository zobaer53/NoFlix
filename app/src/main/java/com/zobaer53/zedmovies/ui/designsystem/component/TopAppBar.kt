

package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun zedMoviesTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = zedMoviesTheme.colors.primary,
        titleContentColor = zedMoviesTheme.colors.textPrimary,
        navigationIconContentColor = zedMoviesTheme.colors.textPrimary
    )
) {
    CenterAlignedTopAppBar(
        modifier = modifier.padding(horizontal = zedMoviesTheme.spacing.extraMedium),
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun zedMoviesTopAppBar(
    @StringRes titleResourceId: Int,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    zedMoviesTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = titleResourceId),
                style = zedMoviesTheme.typography.semiBold.h4,
                color = zedMoviesTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets
    )
}
