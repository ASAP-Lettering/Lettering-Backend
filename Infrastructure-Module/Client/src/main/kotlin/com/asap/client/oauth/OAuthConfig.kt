package com.asap.client.oauth

import com.asap.client.oauth.platform.KakaoOAuthRetrieveHandler
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfig(
    private val kakaoOAuthRetrieveHandler: KakaoOAuthRetrieveHandler
) {

    @Bean
    @Qualifier("oAuthRetrieveHandlers")
    fun oAuthRetrieveHandlers(): Map<SocialLoginProvider, OAuthRetrieveHandler>{
        return mapOf(
            SocialLoginProvider.KAKAO to kakaoOAuthRetrieveHandler as OAuthRetrieveHandler
        )
    }


}