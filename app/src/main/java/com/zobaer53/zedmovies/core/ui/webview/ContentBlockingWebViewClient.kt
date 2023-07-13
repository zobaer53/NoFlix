package com.zobaer53.zedmovies.core.ui.webview

import android.content.Context
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi


class ContentBlockingWebViewClient(context: Context) : WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()

        // Add your content blocking logic here
        if (shouldBlock(url)) {
            // Return an empty response to block the content
            return WebResourceResponse(null, null, null)
        }

        // Return null to allow loading the original content
        return null
    }

    private val xy = getMiddleScreenPosition(context)
    override fun onPageFinished(view: WebView?, url: String?) {
        // Once the page is finished loading, you can scroll to the desired position.
        view?.scrollTo(xy.first, xy.second)
    }

    private fun shouldBlock(url: String): Boolean {
        // Implement your content blocking rules here
        // Example: Block URLs containing "ad" or "tracking"
        return url.contains("ad") || url.contains("tracking")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (request.isRedirect) {
            // Handle redirects as needed
            return true
        }

        val url = request.url.toString()
        val isPopup = isPopupUrl(url)

        if (isPopup) {
            // Block popups by returning true
            return true
        }

        // Allow loading the original content
        return false
    }

    private fun isPopupUrl(url: String): Boolean {
        // Implement your popup detection logic here
        // Example: Check if the URL contains "popup" or "ad" keywords
        return url.contains("popup") || url.contains("ad")
    }
}
