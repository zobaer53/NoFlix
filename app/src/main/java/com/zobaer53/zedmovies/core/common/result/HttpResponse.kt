
package com.zobaer53.zedmovies.core.common.result

internal interface HttpResponse {
    val statusCode: Int
    val statusMessage: String?
    val url: String?
}
