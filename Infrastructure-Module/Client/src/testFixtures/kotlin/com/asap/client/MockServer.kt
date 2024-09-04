package com.asap.client

interface MockServer {

    fun start()

    fun enqueue(
        response: Response
    )

    fun shutdown()

    fun url(baseUrl: String): String


    data class Response(
        val responseCode: Int,
        val body: String,
        val headers: Map<String, String>
    )


}