package com.asap.client.oauth

import com.asap.client.oauth.platform.GoogleOAuthRetrieveHandler
import com.asap.client.oauth.platform.KakaoOAuthRetrieveHandler
import com.asap.client.oauth.platform.NaverOAuthRetrieveHandler
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfig(
    private val kakaoOAuthRetrieveHandler: KakaoOAuthRetrieveHandler,
    private val googleOAuthRetrieveHandler: GoogleOAuthRetrieveHandler,
    private val naverOAuthRetrieveHandler: NaverOAuthRetrieveHandler,
) {
    @Bean
    @Qualifier("oAuthRetrieveHandlers")
    fun oAuthRetrieveHandlers(): Map<SocialLoginProvider, OAuthRetrieveHandler> =
        mapOf(
            SocialLoginProvider.KAKAO to kakaoOAuthRetrieveHandler as OAuthRetrieveHandler,
            SocialLoginProvider.GOOGLE to googleOAuthRetrieveHandler as OAuthRetrieveHandler,
            SocialLoginProvider.NAVER to naverOAuthRetrieveHandler as OAuthRetrieveHandler,
        )
}
