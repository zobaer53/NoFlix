package com.zobaer53.zedmovies.ui.webview

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zobaer53.zedmovies.R
@Composable
fun backgroundTaskScreen(
    websiteUrl: String,
    movieName: String,
    movieYear: String,
    apiType: String,
    lifecycleOwner: LifecycleOwner
):String {
    var url by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val taskResult = withContext(Dispatchers.IO) {
                performBackgroundTask("$websiteUrl$movieName", movieYear,movieName,apiType)
            }
            if (taskResult != null) {
                year = taskResult.first
                title = taskResult.second
                url = taskResult.third
                Log.i("movieLink3", "link 0 url-{$url $year $title}")
            }
        }
    }
    Log.i("movieLink3", "link 1 sflix.to$url")
    if (url.isNotEmpty()) {
        return url
    } else if (year.isNotEmpty() && year != "serverError" && year.toLong()>0) {
        NotFoundProgressDialog(1000)
    }
    else if(year.isNotEmpty() && year == "serverError"){
        NotFoundProgressDialog(1000)
    }else ProgressDialog(toLong = 1000)
return url.ifEmpty { "" }
}

@Composable
fun NotFoundProgressDialog(toLong: Long) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFD1C4E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.data))

        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxOf(200.dp)),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
        Text(text = "Not Found", fontSize = 20.sp)
        // Use DisposableEffect to trigger finish after 1 second when the composable is first composed
        val scope = rememberUpdatedState(LocalContext.current as? ComponentActivity)
        DisposableEffect(Unit) {
            val handler = Handler(Looper.getMainLooper())
            val finishTask = Runnable {
                scope.value?.finish()
            }
            handler.postDelayed( finishTask, toLong)
            onDispose {
                handler.removeCallbacks(finishTask) // Remove the callback to avoid leaks
            }

        }
    }
}

@Composable
fun ProgressDialog(toLong: Long) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFD1C4E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.data))

        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxOf(200.dp)),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}

suspend fun performBackgroundTask(
    websiteUrl: String,
    movieYear: String,
    movieName: String,
    apiType:String
): Triple<String, String, String>? {
    // Simulate a long-running task
    kotlinx.coroutines.delay(3000)
    return scrapeMovieData(websiteUrl, movieYear,movieName,apiType)
}
