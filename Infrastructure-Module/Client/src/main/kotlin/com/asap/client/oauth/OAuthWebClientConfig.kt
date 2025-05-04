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
    fun kakaoWebClient(): WebClient =
        WebClient
            .builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()

    @Bean
    @Qualifier("googleWebClient")
    fun googleWebClient(): WebClient =
        WebClient
            .builder()
            .baseUrl("https://www.googleapis.com")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()

    @Bean
    @Qualifier("naverWebClient")
    fun naverWebClient(): WebClient =
        WebClient
            .builder()
            .baseUrl("https://openapi.naver.com")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()

    @Bean
    @Qualifier("getNaverAccessTokenWebClient")
    fun getNaverAccessTokenWebClient(): WebClient =
        WebClient
            .builder()
            .baseUrl("https://nid.naver.com")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build()
}
