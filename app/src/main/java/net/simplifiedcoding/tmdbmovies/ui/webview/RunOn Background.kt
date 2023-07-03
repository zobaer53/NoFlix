package net.simplifiedcoding.tmdbmovies.ui.webview

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.simplifiedcoding.tmdbmovies.ui.common.FullScreenProgressbar

@Composable
fun BackgroundTaskScreen(websiteUrl: String) {
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val taskResult = withContext(Dispatchers.IO) {
                performBackgroundTask(websiteUrl)
            }
            if (taskResult != null) {
                result = taskResult
                Log.i("movieLink3","link 0 sflix.to$result")
            }
        }

    }
    Log.i("movieLink3","link 1 sflix.to$result")
    if(result.isNotEmpty()){
        VideoWebView(url = "https://sflix.to$result")
    }else{
        FullScreenProgressbar()
    }

}

suspend fun performBackgroundTask(websiteUrl: String): String? {
    // Simulate a long-running task
    kotlinx.coroutines.delay(3000)
    return scrapeMovieData(websiteUrl)

}
