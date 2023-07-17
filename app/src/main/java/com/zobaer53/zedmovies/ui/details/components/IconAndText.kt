
package com.zobaer53.zedmovies.ui.details.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
internal fun IconAndText(
    @DrawableRes iconResourceId: Int,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = zedMoviesTheme.colors.textSecondary,
    isPlaceholder: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraSmall)
    ) {
        Icon(
            modifier = Modifier.size(IconAndTextIconSize),
            painter = painterResource(id = iconResourceId),
            contentDescription = text,
            tint = color
        )
        Text(
            modifier = if (isPlaceholder) {
                Modifier
                    .fillMaxWidth(PlaceholderTextMaxWidthFraction)
                    .height(PlaceholderTextHeight)
                    .zedMoviesPlaceholder(color = color)
            } else {
                Modifier
            },
            text = text,
            style = zedMoviesTheme.typography.medium.h5,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun IconAndTextPlaceholder(
    @DrawableRes iconResourceId: Int,
    modifier: Modifier = Modifier
) {
    IconAndText(
        modifier = modifier,
        iconResourceId = iconResourceId,
        text = PlaceholderText,
        isPlaceholder = true
    )
}

private val IconAndTextIconSize = 18.dp
private val PlaceholderTextHeight = IconAndTextIconSize / 1.5f
private const val PlaceholderText = ""
private const val PlaceholderTextMaxWidthFraction = 0.5f
