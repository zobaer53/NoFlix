package com.zobaer53.zedmovies.ui.webview

/*

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
*/
import android.graphics.Bitmap
import android.net.Proxy
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.Response
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AdBlockingWebViewClient(private val okHttpClient: OkHttpClient) : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        try {
            val okHttpRequest = Request.Builder()
                .url(request?.url.toString())
                .build()

            val response = okHttpClient.newCall(okHttpRequest).execute()
            val contentType = response.header("content-type")
            val inputStream = response.body?.byteStream()

            return if (contentType != null && inputStream != null) {
                WebResourceResponse(contentType, "UTF-8", inputStream)
            } else {
                val adBlocker = AdBlocker()

                if (adBlocker.shouldBlock(request?.url?.toString() ?: "")) {
                    val emptyStream = ByteArrayInputStream(byteArrayOf())
                    return WebResourceResponse("text/plain", "utf-8", emptyStream)
                }

                return super.shouldInterceptRequest(view, request)
            }

        } catch (e: IOException) {
            return super.shouldInterceptRequest(view, request)
        }
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val adBlocker = AdBlocker()

        if (adBlocker.shouldBlock(url ?: "")) {
            val emptyStream = ByteArrayInputStream(byteArrayOf())
            return WebResourceResponse("text/plain", "utf-8", emptyStream)
        }

        return super.shouldInterceptRequest(view, url)
    }
}

class AdBlocker {
    private val adDomains = setOf(
        "ad.doubleclick.net",
        "googleads.g.doubleclick.net",
        // Add more ad domains as needed
    )

    fun shouldBlock(url: String): Boolean {
        return adDomains.any { domain -> url.contains(domain) }
    }
}

