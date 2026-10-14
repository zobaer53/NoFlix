
package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.SubcomposeAsyncImageScope
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun ZedMoviesNetworkImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = model,
        contentDescription = contentDescription,
        contentScale = contentScale
    ) {
        SubcomposeAsyncImageHandler() }
}

@Composable
fun ZedMoviesCardNetworkImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = zedMoviesTheme.shapes.default
) {
    Card(modifier = modifier, shape = shape) {
        ZedMoviesNetworkImage(
            modifier = Modifier.fillMaxSize(),
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale
        )
    }
}

@Composable
private fun SubcomposeAsyncImageScope.SubcomposeAsyncImageHandler() {
    val state by painter.state.collectAsState()
    when (state) {
        is AsyncImagePainter.State.Loading -> zedMoviesImagePlaceholder()
        is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
        AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Error -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(zedMoviesTheme.colors.primaryVariant)
        )
    }
}
