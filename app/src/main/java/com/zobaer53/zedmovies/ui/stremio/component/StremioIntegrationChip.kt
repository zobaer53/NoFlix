package com.zobaer53.zedmovies.ui.stremio.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

/**
 * A ready-to-use Compose chip that can be placed on NoFlix's Movie & TV Details screens
 * next to the "Play" and "Trailer" buttons.
 *
 * Example usage in DetailsScreen:
 * ```kotlin
 * StremioIntegrationChip(
 *     onClick = {
 *         // Navigate to Stremio screen or open BottomSheet with movieDetails.imdbId
 *         onOpenStremioStreams(movieDetails.imdbId, movieDetails.title, "movie")
 *     }
 * )
 * ```
 */
@Composable
fun StremioIntegrationChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Stremio Streams"
) {
    Row(
        modifier = modifier
            .background(
                color = zedMoviesTheme.colors.accentContainer,
                shape = zedMoviesTheme.shapes.small
            )
            .clickable { onClick() }
            .padding(
                horizontal = zedMoviesTheme.spacing.small,
                vertical = zedMoviesTheme.spacing.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_play),
            contentDescription = label,
            tint = zedMoviesTheme.colors.accent,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(zedMoviesTheme.spacing.extraSmall))

        Text(
            text = label,
            style = zedMoviesTheme.typography.semiBold.h6,
            color = zedMoviesTheme.colors.accent
        )
    }
}
