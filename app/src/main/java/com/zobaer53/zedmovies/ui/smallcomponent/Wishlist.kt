
package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesIconButton
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.R

@Composable
fun zedMoviesWishlistButton(
    isWishlisted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    zedMoviesIconButton(
        modifier = modifier
            .size(zedMoviesWishlistButtonShapeSize)
            .background(
                color = zedMoviesTheme.colors.primaryVariant,
                shape = zedMoviesTheme.shapes.smallMedium
            ),
        iconResourceId = R.drawable.ic_wishlist,
        contentDescription = stringResource(id = R.string.wishlist),
        onClick = onClick,
        tint = zedMoviesTheme.colors.error.let { color ->
            if (isWishlisted) color else color.copy(alpha = DisabledContentAlpha)
        }
    )
}

private val zedMoviesWishlistButtonShapeSize = 32.dp
private const val DisabledContentAlpha = 0.5f
