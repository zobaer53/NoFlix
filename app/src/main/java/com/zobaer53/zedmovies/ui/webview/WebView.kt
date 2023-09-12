package com.zobaer53.zedmovies.ui.webview

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import io.github.edsuns.adfilter.AdFilter
import okhttp3.OkHttpClient
import java.net.InetSocketAddress

@SuppressLint("SetJavaScriptEnabled", "RequiresFeature")
@Composable
fun VideoWebView(url: String, lifecycleOwner: LifecycleOwner) {
    val filter = AdFilter.get()
    val filterViewModel = filter.viewModel

    // Setup AdblockAndroid for your WebView.

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                filter.setupWebView(this)
                // Add filter list subscriptions on first installation.
                if (!filter.hasInstallation) {
                    val map = mapOf(
                        "AdGuard Base" to "https://filters.adtidy.org/extension/chromium/filters/2.txt",
                        "EasyPrivacy Lite" to "https://filters.adtidy.org/extension/chromium/filters/118_optimized.txt",
                        "AdGuard Tracking Protection" to "https://filters.adtidy.org/extension/chromium/filters/3.txt",
                        "AdGuard Annoyances" to "https://filters.adtidy.org/extension/chromium/filters/14.txt",
                        "AdGuard Chinese" to "https://filters.adtidy.org/extension/chromium/filters/224.txt",
                        "NoCoin Filter List" to "https://filters.adtidy.org/extension/chromium/filters/242.txt"
                    )
                    for ((key, value) in map) {
                        val subscription = filterViewModel.addFilter(key, value)
                        filterViewModel.download(subscription.id)
                    }
                }

                filterViewModel.onDirty.observe(lifecycleOwner) {
                    // Clear cache when there are changes to the filter.
                    // You need to refresh the page manually to make the changes take effect.
                    clearCache(false)
                }

                // Enable JavaScript and other settings
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    WebSettingsCompat.setForceDark(this, WebSettingsCompat.FORCE_DARK_OFF)
                }
                val proxyHost = "91.211.245.176"
                val proxyPort = 8080 // Your proxy port

                val okHttpClient = OkHttpClient.Builder()
                    .proxy(java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)))
                    .build()

                // Set WebViewClient and WebChromeClient
                //webViewClient = AdBlockingWebViewClient(okHttpClient)
                webViewClient= MyWebClient()

                // Load the video URL
                loadUrl(url)
            }
        }
    )
}
