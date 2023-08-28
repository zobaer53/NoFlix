package com.zobaer53.zedmovies.ui.webview

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewFeature
import androidx.webkit.WebSettingsCompat
import okhttp3.OkHttpClient
import java.net.InetSocketAddress


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoWebView(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Enable JavaScript and other settings
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                }
                val proxyHost = "91.211.245.176"
                val proxyPort = 8080 // Your proxy port

                val okHttpClient = OkHttpClient.Builder()
                    .proxy(java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)))
                    .build()

                // Set WebViewClient and WebChromeClient
                webViewClient = AdBlockingWebViewClient(okHttpClient)
                webChromeClient = WebChromeClient()

                // Load the video URL
                loadUrl(url)
            }
        }
    )

}
