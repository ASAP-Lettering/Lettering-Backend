package com.asap.client

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
    fun kakaoMockWebServer(): MockWebServer {
        return MockWebServer().apply {

            url("/v2/user/m2")
        }
    }

    @Bean
    @Qualifier("kakaoWebClient")
    @Primary
    fun mockKakaoWebClient(kakaoMockWebServer: MockWebServer): WebClient {
        return WebClient.builder()
            .baseUrl(kakaoMockWebServer.url("/").toString())
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }


}