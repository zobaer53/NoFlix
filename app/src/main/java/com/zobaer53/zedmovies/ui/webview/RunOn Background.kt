package com.zobaer53.zedmovies.ui.webview

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BackgroundTaskScreen(websiteUrl: String, movieName: String, movieYear: String) {
    var url by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val taskResult = withContext(Dispatchers.IO) {
                performBackgroundTask("$websiteUrl$movieName", movieYear,movieName)
            }
            if (taskResult != null) {
                year = taskResult.first
                title = taskResult.second
                url = taskResult.third
                Log.i("movieLink3", "link 0 sflix.to{$url $year $title}")
            }
        }
    }
    Log.i("movieLink3", "link 1 sflix.to$url")
    if (url.isNotEmpty()) {
        VideoWebView(url = "https://sflix.to$url")
    } else  {
        CircularProgressDialog()
    }
}

@Composable
fun CircularProgressDialog() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFD1C4E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colors.primary
        )
        // Use DisposableEffect to trigger finish after 1 second when the composable is first composed
        val scope = rememberUpdatedState(LocalContext.current as? ComponentActivity)
        DisposableEffect(Unit) {
            val handler = Handler(Looper.getMainLooper())
            val finishTask = Runnable {
                scope.value?.finish()
            }
            handler.postDelayed( finishTask, 5000L)
            onDispose {
                handler.removeCallbacks(finishTask) // Remove the callback to avoid leaks
            }
        }
    }
}

suspend fun performBackgroundTask(
    websiteUrl: String,
    movieYear: String,
    movieName: String
): Triple<String, String, String>? {
    // Simulate a long-running task
    kotlinx.coroutines.delay(3000)
    return scrapeMovieData(websiteUrl, movieYear,movieName)

}
