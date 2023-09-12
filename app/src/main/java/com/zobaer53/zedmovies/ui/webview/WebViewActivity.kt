package com.zobaer53.zedmovies.ui.webview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.edsuns.adfilter.AdFilter


class WebViewActivity : ComponentActivity()  {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val filter = AdFilter.create(this)
            val movieName = intent.getStringExtra("movieName") ?: ""
            val movieYear = intent.getStringExtra("movieYear") ?: ""
            val replacedString = movieName.replace(" ", "-").lowercase()
        setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val websiteUrl = "https://sflix.to/search/"
                    BackgroundTaskScreen(websiteUrl,replacedString,movieYear,this)
                }

        }
    }
}

