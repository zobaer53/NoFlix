package com.zobaer53.zedmovies.ui.webview

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BackgroundTaskScreen(websiteUrl: String, replacedString: String) {
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val taskResult = withContext(Dispatchers.IO) {
                performBackgroundTask("$websiteUrl$replacedString")
            }
            if (taskResult != null) {
                result = taskResult
                Log.i("movieLink3", "link 0 sflix.to$result")
            }
        }
    }
    Log.i("movieLink3", "link 1 sflix.to$result")
    if (result.isNotEmpty() && result.contains(replacedString)) {
        VideoWebView(url = "https://sflix.to$result")
    } else {
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
    }
}

suspend fun performBackgroundTask(websiteUrl: String): String? {
    // Simulate a long-running task
    kotlinx.coroutines.delay(3000)
    return scrapeMovieData(websiteUrl)

}
