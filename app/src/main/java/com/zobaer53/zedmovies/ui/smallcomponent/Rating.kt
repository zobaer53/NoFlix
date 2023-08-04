

package com.zobaer53.zedmovies.ui.smallcomponent

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesIcon
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.ui.webview.WebViewActivity
import com.zobaer53.zedmovies.R

@Composable
fun RatingItem(
    title: String,
    releaseDate: String,
    rating: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        shape = zedMoviesTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = zedMoviesTheme.colors.primaryVariant.copy(alpha = RatingItemContainerColorAlpha),
            contentColor = zedMoviesTheme.colors.secondaryVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = zedMoviesTheme.spacing.small,
                vertical = zedMoviesTheme.spacing.extraSmall
            )
        ) {
            if(rating == 0.0){
                zedMoviesIcon(
                    modifier = Modifier.size(30.dp)
                        .clickable{
                            /*Toast.makeText(context,"clicked", Toast.LENGTH_LONG).show()*/
                            val intent = Intent(context, WebViewActivity::class.java).apply {
                                putExtra("movieName", title)
                                putExtra("movieYear", releaseDate)
                            }
                            context.startActivity(intent)

                        },
                    iconResourceId = R.drawable.ic_play,
                    contentDescription = stringResource(id = R.string.rating)
                )
                Spacer(modifier = Modifier.width(zedMoviesTheme.spacing.extraSmall))
                Text(
                    text = "Play",
                    style = zedMoviesTheme.typography.semiBold.h6
                )

            }else{
                zedMoviesIcon(
                    modifier = Modifier.size(RatingIconSize),
                    iconResourceId = R.drawable.ic_star,
                    contentDescription = stringResource(id = R.string.rating)
                )
                Spacer(modifier = Modifier.width(zedMoviesTheme.spacing.extraSmall))
                Text(
                    text = if (rating == RatingNotRatedValue) {
                        stringResource(id = R.string.not_rated)
                    } else {
                        rating.toString()
                    },
                    style = zedMoviesTheme.typography.semiBold.h6
                )
            }

        }
    }
}

private val RatingIconSize = 16.dp
private const val RatingItemContainerColorAlpha = 0.72f
private const val RatingNotRatedValue = 0.0
