package com.zobaer53.zedmovies.ui.webview

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

suspend fun scrapeMovieData(url: String, movieYear: String, movieName: String): Triple<String, String, String>? {
    val movieList = Triple("","","")
    var movieNameReplaced = movieName.trim().replace("-"," ")
    try {
        val doc: Document = Jsoup.connect(url).get()
        val flwItems: Elements = doc.select(".flw-item")

        for (i in 0 until flwItems.size) {
            val flwItem = flwItems[i]
            val year = flwItem.select(".fdi-item").first()?.text() ?: ""
            val type = flwItem.select(".fdi-item strong").first()?.text() ?: ""
            val title = flwItem.select(".film-name a").first()?.text() ?: "".lowercase()
            val movieUrl = flwItem.select(".film-name a").first()?.attr("href") ?: ""

            Log.i("movieLink3", "link ------------------------------ sflix.to{$movieUrl $year ${title.lowercase()} main $movieNameReplaced $movieYear}")
            if(title.trim().lowercase().contains(movieNameReplaced) && movieYear == year) {
                Log.i("movieLink3", "------------------------------link 0 sflix.to{$movieUrl $year $title}")
                return movieList.copy(year, title, movieUrl)
            }

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


