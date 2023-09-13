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

