package com.zobaer53.zedmovies.core.ui.webview

import android.util.Log
import org.jsoup.Jsoup

fun scrapeMovieData(url: String):String? {
    val doc = Jsoup.connect(url).get()
    val link = getLink(doc.html())
    Log.i("movieLink2","Link URL---------------: $link")

    // Select the appropriate HTML elements that contain movie data
    val movieElements = doc.select(".film-poster")
    Log.i("movieLink2",".film-poster $movieElements")

    return link
}
fun getLink(html: String): String? {
    val doc = Jsoup.parse(html)
    val filmPosterElement = doc.selectFirst(".film-poster")
    val linkElement = filmPosterElement?.selectFirst("a.film-poster-ahref")

    return linkElement?.attr("href")
}
