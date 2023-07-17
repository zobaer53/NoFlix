
package com.zobaer53.zedmovies.data.common.result

internal interface HttpResponse {
    val statusCode: Int
    val statusMessage: String?
    val url: String?
}
