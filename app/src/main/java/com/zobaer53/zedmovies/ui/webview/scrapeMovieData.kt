package com.zobaer53.zedmovies.ui.webview

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

fun scrapeMovieData(
    url: String,
    movieYear: String,
    movieName: String,
    apiType: String
): Triple<String, String, String>? {
    val url2 = "https://1movieshd.com/search/$movieName"
    var startTime: Long
    var totalTime: Long = 0
    val movieList = Triple("", "", "")
    val movieNameReplaced = movieName.trim().replace("-", " ")
    try {
        val doc: Document = Jsoup.connect(url).get()
        val statusCode = doc.connection().response().statusCode()
        Log.i("movieLink3", "123 ------------ $statusCode api name= $movieName api year= $movieYear")
        if (statusCode == 200) {
            val flwItems: Elements = doc.select(".flw-item")

            for (i in 0 until flwItems.size) {
                val flwItem = flwItems[i]
                val year = flwItem.select(".fdi-item").first()?.text() ?: ""
                val type = flwItem.select(".fdi-item strong").first()?.text() ?: ""
                val title = flwItem.select(".film-name a").first()?.text() ?: "".lowercase()
                val movieUrl = flwItem.select(".film-name a").first()?.attr("href") ?: ""

                Log.i(
                    "movieLink3",
                    "link -type= $type && web sflix.to{$movieUrl $year ${title.lowercase()} main $movieNameReplaced $movieYear}"
                )
                if (title.trim().lowercase().contains(movieNameReplaced)&& type.trim().lowercase().contains(apiType) || movieYear == year && type.trim().lowercase().contains(apiType)) {
                    Log.i(
                        "movieLink3",
                        "link final type= $type sflix.to{$movieUrl $year $title}"
                    )
                    return movieList.copy(year, title, "https://sflix.to$movieUrl")
                }
            }
            startTime = System.currentTimeMillis()
            try {
                val doc2: Document = Jsoup.connect(url2).get()
                val statusCode2 = doc2.connection().response().statusCode()
                Log.i("movieLink3", "------------------------------ $statusCode")
                if (statusCode2 == 200) {
                    val flwItems2: Elements = doc2.select(".flw-item")
                    for (i in 0 until flwItems2.size / 2) {
                        val flwItem2 = flwItems2[i]
                        val year2 = flwItem2.select(".fdi-item").first()?.text() ?: ""
                        val type2 = flwItem2.select(".fdi-item strong").first()?.text() ?: ""
                        val title2 =
                            flwItem2.select(".film-name a").first()?.text() ?: "".lowercase()
                        val movieUrl2 = flwItem2.select(".film-name a").first()?.attr("href") ?: ""

                        Log.i(
                            "movieLink3",
                            "link ------------------------------ 1movieshd.com{$movieUrl2 $year2 ${title2.lowercase()} main $movieNameReplaced $movieYear}"
                        )
                        if (title2.trim().lowercase()
                                .contains(movieNameReplaced)&& type2.trim().lowercase().contains(apiType) || movieYear == year2 && type2.trim().lowercase().contains(apiType)
                        ) {
                            Log.i(
                                "movieLink3",
                                "------------------------------link final 1movieshd.com{$movieUrl2 $year2 $title2}"
                            )
                            return movieList.copy(year2, title2, "https://1movieshd.com$movieUrl2")
                        }
                    }
                    val endTime = System.currentTimeMillis()
                    totalTime = endTime - startTime
                } else if (statusCode2 == 521) {
                    return movieList.copy("serverError", "", "")
                }
                return if (totalTime.toInt() != 0) {
                    movieList.copy(totalTime.toString(), "", "")
                }
                else null

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (statusCode == 521) {
            startTime = System.currentTimeMillis()
            try {
                val doc2: Document = Jsoup.connect(url2).get()
                val statusCode2 = doc2.connection().response().statusCode()
                Log.i("movieLink3", "------------------------------ $statusCode")
                if (statusCode2 == 200) {
                    val flwItems2: Elements = doc2.select(".flw-item")
                    for (i in 0 until flwItems2.size / 2) {
                        val flwItem2 = flwItems2[i]
                        val year2 = flwItem2.select(".fdi-item").first()?.text() ?: ""
                        val type2 = flwItem2.select(".fdi-item strong").first()?.text() ?: ""
                        val title2 =
                            flwItem2.select(".film-name a").first()?.text() ?: "".lowercase()
                        val movieUrl2 = flwItem2.select(".film-name a").first()?.attr("href") ?: ""

                        Log.i(
                            "movieLink3",
                            "link ------------------------------ 1movieshd.com{$movieUrl2 $year2 ${title2.lowercase()} main $movieNameReplaced $movieYear}"
                        )
                        if (title2.trim().lowercase()
                                .contains(movieNameReplaced) && type2.trim().lowercase().contains(apiType) || movieYear == year2 && type2.trim().lowercase().contains(apiType)
                        ) {
                            Log.i(
                                "movieLink3",
                                "------------------------------link final 1movieshd.com{$movieUrl2 $year2 $title2}"
                            )
                            return movieList.copy(year2, title2, "https://1movieshd.com$movieUrl2")
                        }
                    }
                    val endTime = System.currentTimeMillis()
                    totalTime = endTime - startTime
                } else if (statusCode2 == 521) {
                    return movieList.copy("serverError", "", "")
                }
                return if (totalTime.toInt() != 0) {
                    movieList.copy(totalTime.toString(), "", "")
                }
                else null

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return if (totalTime.toInt() != 0) {
        movieList.copy(totalTime.toString(), "", "")
    }
    else null
}


