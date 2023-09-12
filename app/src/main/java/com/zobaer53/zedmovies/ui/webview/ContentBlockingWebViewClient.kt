package com.zobaer53.zedmovies.ui.webview

import android.graphics.Bitmap
import android.net.Proxy
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.Response
import io.github.edsuns.adfilter.AdFilter
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
class MyWebClient() : WebViewClient() {

    private val filter = AdFilter.get()

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val result = filter.shouldIntercept(view!!, request!!)
        return result.resourceResponse
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        filter.performScript(view, url)
    }
}

