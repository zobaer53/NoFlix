package net.simplifiedcoding.tmdbmovies.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.simplifiedcoding.tmdbmovies.ui.theme.AppTheme

class WebViewActivity : ComponentActivity() {
        @SuppressLint("CoroutineCreationDuringComposition")
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val movieName = intent.getStringExtra("movieName") ?: ""
            val replacedString = movieName.replace(" ", "-").lowercase()
        setContent {
            val context = LocalContext.current
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                  //  VideoWebView(url = movies[0])
                    val websiteUrl = "https://sflix.to/search/$replacedString"
                    BackgroundTaskScreen(websiteUrl)
                }
            }
        }
    }
}

