package com.asap.client.oauth

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class OAuthWebClientConfig {

    @Bean
    @Qualifier("kakaoWebClient")
    fun kakaoWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}