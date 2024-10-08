package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import com.asap.client.oauth.exception.OAuthException
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoOAuthRetrieveHandler(
    @Qualifier("kakaoWebClient") private val kakaoWebClient: WebClient,
) : OAuthRetrieveHandler {
    override fun getOAuthInfo(request: OAuthRetrieveHandler.OAuthRequest): OAuthRetrieveHandler.OAuthResponse =
        kakaoWebClient
            .get()
            .uri("/v2/user/me")
            .header("Authorization", "Bearer ${request.accessToken}")
            .retrieve()
            .onStatus({ it.isError }, {
                throw OAuthException.OAuthRetrieveFailedException("카카오 사용자 정보를 가져오는데 실패했습니다.")
            })
            .bodyToMono(KakaoUserInfo::class.java)
            .block()
            ?.let {
                OAuthRetrieveHandler.OAuthResponse(
                    username = it.properties.nickname,
                    socialId = it.id,
                    profileImage = it.properties.profileImage,
                    email = it.kakaoAccount.email,
                )
            } ?: throw OAuthException.OAuthRetrieveFailedException("카카오 사용자 정보를 가져오는데 실패했습니다.")

    data class KakaoUserInfo(
        val id: String,
        val properties: Properties,
        @field:JsonProperty("kakao_account")
        val kakaoAccount: KakaoAccount,
    )

    data class Properties(
        val nickname: String,
        @field:JsonProperty("profile_image")
        val profileImage: String,
    )

    data class KakaoAccount(
        val email: String,
    )
}
