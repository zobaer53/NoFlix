package net.simplifiedcoding.tmdbmovies.ui.movies

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.simplifiedcoding.tmdbmovies.R
import net.simplifiedcoding.tmdbmovies.data.models.Movie
import net.simplifiedcoding.tmdbmovies.ui.theme.AppTheme
import net.simplifiedcoding.tmdbmovies.ui.theme.spacing
import net.simplifiedcoding.tmdbmovies.ui.webview.WebViewActivity


@Composable
fun MovieItem(movie: Movie) {
    val spacing = MaterialTheme.spacing
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface),
                    start = Offset(0f, Float.POSITIVE_INFINITY),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(spacing.extraSmall)
            .clip(RoundedCornerShape(spacing.small))
            .shadow(elevation = 1.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(spacing.small)
                .fillMaxWidth().clickable {
                    val intent = Intent(context,WebViewActivity::class.java).apply {
                        val stringValue = movie.title
                        putExtra("movieName", stringValue)
                    }
                    context.startActivity(intent)
                }
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.fullPosterPath)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.bg_image_placeholder),
                contentDescription = movie.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.weight(0.4f)
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = spacing.medium)
            ) {
                Text(
                    text = movie.originalTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(spacing.medium))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(spacing.medium))

                Text(
                    text = "IMDB ${movie.voteAverage}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.extraSmall))
                        .background(Color.Yellow)
                        .padding(
                            start = spacing.small,
                            end = spacing.small,
                            top = spacing.extraSmall,
                            bottom = spacing.extraSmall
                        ).clickable {
                            val intent = Intent(context,WebViewActivity::class.java).apply {
                                val stringValue = movie.title
                                putExtra("movieName", stringValue)
                            }
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}

val movie = Json.decodeFromString<Movie>("{\"adult\":false,\"backdrop_path\":\"\\/y2Ca1neKke2mGPMaHzlCNDVZqsK.jpg\",\"genre_ids\":[28,35,53],\"id\":718930,\"original_language\":\"en\",\"original_title\":\"Bullet Train\",\"overview\":\"Unlucky assassin Ladybug is determined to do his job peacefully after one too many gigs gone off the rails. Fate, however, may have other plans, as Ladybug's latest mission puts him on a collision course with lethal adversaries from around the globe\\u2014all with connected, yet conflicting, objectives\\u2014on the world's fastest train.\",\"popularity\":5159.444,\"poster_path\":\"\\/tVxDe01Zy3kZqaZRNiXFGDICdZk.jpg\",\"release_date\":\"2022-07-03\",\"title\":\"Bullet Train\",\"video\":false,\"vote_average\":7.5,\"vote_count\":1407}")

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MovieItemPreviewDark() {
    AppTheme {
        MovieItem(movie)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MovieItemPreviewLight() {
    AppTheme {
        MovieItem(movie)
    }
}
