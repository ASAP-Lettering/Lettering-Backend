package com.asap.client

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class TestWebClientConfig {
    @Bean
    fun kakaoMockWebServer(): MockServer {
        val mockWebServer =
            MockWebServer().apply {
                url("/v2/user/me")
            }
        return object : MockServer {
            override fun start() {
                mockWebServer.start()
            }

            override fun enqueue(response: MockServer.Response) {
                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(response.responseCode)
                        .setBody(response.body)
                        .also {
                            response.headers.forEach { (key, value) -> it.addHeader(key, value) }
                        },
                )
            }

            override fun shutdown() {
                mockWebServer.shutdown()
            }

            override fun url(baseUrl: String): String = mockWebServer.url(baseUrl).toString()
        }
    }

    @Bean
    @Qualifier("kakaoWebClient")
    @Primary
    fun mockKakaoWebClient(kakaoMockWebServer: MockServer): WebClient =
        WebClient
            .builder()
            .baseUrl(kakaoMockWebServer.url("/"))
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
}
