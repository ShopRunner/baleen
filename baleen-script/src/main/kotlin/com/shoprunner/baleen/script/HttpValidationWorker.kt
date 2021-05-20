package com.shoprunner.baleen.script

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration

class HttpValidationWorker(private val dsl: BaleenValidation) {
    val httpClient = HttpClient.newHttpClient()

    private enum class Method {
        GET, POST, PUT, DELETE
    }

    private fun makeRequest(url: String, method: Method, contentType: String?, requestBody: Any? = null): HttpResponse<String?> {
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
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun get(url: String, contentType: String?, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.GET, contentType)
        dsl.body(response.body())
    }

    fun post(url: String, contentType: String?, requestBody: String, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.POST, contentType, requestBody)
        dsl.body(response.body())
    }

    fun post(url: String, contentType: String?, requestBody: Path, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.POST, contentType, requestBody)
        dsl.body(response.body())
    }

    fun post(url: String, contentType: String?, requestBody: File, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.POST, contentType, requestBody)
        dsl.body(response.body())
    }

    fun put(url: String, contentType: String?, requestBody: String, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.PUT, contentType, requestBody)
        dsl.body(response.body())
    }

    fun put(url: String, contentType: String?, requestBody: Path, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.PUT, contentType, requestBody)
        dsl.body(response.body())
    }

    fun put(url: String, contentType: String?, requestBody: File, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.PUT, contentType, requestBody)
        dsl.body(response.body())
    }

    fun delete(url: String, contentType: String?, body: BaleenValidation.(String?) -> Unit) {
        val response = makeRequest(url, Method.GET, contentType)
        dsl.body(response.body())
    }
}
