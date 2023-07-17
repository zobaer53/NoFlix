

package com.zobaer53.zedmovies.ui.details.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesTopAppBar
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesBackButton
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesWishlistButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    title: String,
    isWishlisted: Boolean,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    titleColor: Color = zedMoviesTheme.colors.textPrimary,
    isPlaceholder: Boolean = false
) {
    zedMoviesTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier
                    .padding(horizontal = zedMoviesTheme.spacing.small)
                    .then(
                        if (isPlaceholder) {
                            Modifier
                                .fillMaxWidth()
                                .zedMoviesPlaceholder(color = titleColor)
                        } else {
                            Modifier
                        }
                    ),
                text = title,
                style = zedMoviesTheme.typography.semiBold.h4,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { zedMoviesBackButton(onClick = onBackButtonClick) },
        actions = {
            zedMoviesWishlistButton(isWishlisted = isWishlisted, onClick = onWishlistButtonClick)
        },
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = zedMoviesTheme.colors.textPrimary,
            navigationIconContentColor = zedMoviesTheme.colors.textPrimary
        )
    )
}

@Composable
internal fun TopAppBarPlaceholder(
    isWishlisted: Boolean,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = PlaceholderText,
        isWishlisted = isWishlisted,
        onBackButtonClick = onBackButtonClick,
        onWishlistButtonClick = onWishlistButtonClick,
        isPlaceholder = true
    )
}

private const val PlaceholderText = ""
