package com.shoprunner.baleen.script

import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration

enum class Method {
    GET, POST, PUT, DELETE
}

fun http(url: String, method: Method, contentType: String?, data: HttpDataAccess, requestBody: Any? = null): DataAccess =
    { dataDescription ->
        val response = makeRequest(url, method, contentType, requestBody)
        data(response.body())(dataDescription)
    }

fun makeRequest(url: String, method: Method, contentType: String?, requestBody: Any? = null): HttpResponse<InputStream?> {
    val httpClient = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder().apply {
        uri(URI.create(url))
        timeout(Duration.ofMinutes(1))
        if (contentType != null) {
            header("Content-Type", contentType)
        }
        when (method) {
            Method.GET -> this.GET()
            Method.POST -> when (requestBody) {
                is String -> this.POST(HttpRequest.BodyPublishers.ofString(requestBody))
                is Path -> this.POST(HttpRequest.BodyPublishers.ofFile(requestBody))
                is File -> this.POST(HttpRequest.BodyPublishers.ofFile(requestBody.toPath()))
            }
            Method.PUT -> when (requestBody) {
                is String -> this.PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                is Path -> this.PUT(HttpRequest.BodyPublishers.ofFile(requestBody))
                is File -> this.POST(HttpRequest.BodyPublishers.ofFile(requestBody.toPath()))
            }
            Method.DELETE -> this.DELETE()
        }
    }.build()
    return httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
}
