package com.zobaer53.zedmovies.ui.webview

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream

// Simplified WebViewClient without ad-blocking to prevent crash
class MyWebClient : WebViewClient() {
    private val TAG = "MyWebClient"
    
    // Safely handle ad filtering without using the native library directly
    private val adBlockedDomains = setOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "adservice.google.com",
        "adnxs.com",
        "adcolony.com",
        "adform.net",
        "adsafeprotected.com",
        "moatads.com",
        "facebook.com/tr",
        "amazon-adsystem.com",
        "rubiconproject.com",
        "scorecardresearch.com",
        "track",
        "pixel",
        "analytics",
        "metric",
        "banner",
        "popup",
        "advertisement",
        "ga.js",
        "ads"
    )
    
    // Handle external links and potentially block them
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        Log.d(TAG, "Loading URL: $url")
        
        // Keep the playback URL within our WebView
        if (url.contains("sflix.to") || url.contains("1movieshd.com") || url.contains("embed") || url.contains("player") || url.contains("video")) {
            Log.d(TAG, "Allowing video content URL: $url")
            return false // Let WebView handle it
        }
        
        // Block known ad domains and popups
        if (isAdUrl(url)) {
            Log.d(TAG, "Blocking ad URL: $url")
            return true // Block this URL
        }
        
        // For external links that might be legitimate
        if (url.startsWith("http")) {
            Log.d(TAG, "External URL: $url - handling in same WebView")
            return false // Load in the same WebView
        }
        
        return false
    }
    
    // Simple ad blocking logic
    private fun isAdUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host ?: return false
        
        return adBlockedDomains.any { blockedDomain ->
            host.contains(blockedDomain) || url.contains(blockedDomain)
        }
    }
    
    // Intercept requests to block ads
    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()
        
        if (isAdUrl(url)) {
            Log.d(TAG, "Intercepting ad request: $url")
            // Return an empty response to block the request
            return WebResourceResponse(
                "text/plain",
                "UTF-8",
                ByteArrayInputStream("".toByteArray())
            )
        }
        
        return super.shouldInterceptRequest(view, request)
    }
    
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d(TAG, "Page started loading: $url")
    }
    
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(TAG, "Page finished loading: $url")
        
        // Inject JavaScript to block popups and redirects
        val javascript = """
            (function() {
                // Block window.open
                window.open = function() { 
                    console.log('Popup blocked');
                    return null; 
                };
                
                // Block onclick redirects
                var links = document.getElementsByTagName('a');
                for (var i = 0; i < links.length; i++) {
                    links[i].addEventListener('click', function(e) {
                        var href = this.getAttribute('href');
                        if (href && !href.includes('player') && !href.includes('video') && !href.includes('embed')) {
                            e.preventDefault();
                            console.log('Link click blocked: ' + href);
                        }
                    }, true);
                }
                
                // Remove annoying elements
                var elementsToRemove = document.querySelectorAll('.ads, .ad-container, [id*="ads"], [class*="ads"], [id*="google_ads"], [class*="google_ads"], iframe:not([src*="player"]):not([src*="embed"])');
                for (var i = 0; i < elementsToRemove.length; i++) {
                    elementsToRemove[i].style.display = 'none';
                }
            })();
        """.trimIndent()
        
        view?.evaluateJavascript(javascript, null)
    }
}

