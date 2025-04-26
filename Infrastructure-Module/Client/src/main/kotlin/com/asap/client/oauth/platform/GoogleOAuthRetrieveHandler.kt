package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import com.asap.client.oauth.exception.OAuthException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleOAuthRetrieveHandler(
    @Qualifier("googleWebClient") private val googleWebClient: WebClient,
) : OAuthRetrieveHandler {
    override fun getOAuthInfo(request: OAuthRetrieveHandler.OAuthRequest): OAuthRetrieveHandler.OAuthResponse {
        val googleUserInfo =
            googleWebClient
                .get()
                .uri("/userinfo/v2/me")
                .header("Authorization", "Bearer ${request.accessToken}")
                .retrieve()
                .onStatus({ it.isError }, {
                    throw OAuthException.OAuthRetrieveFailedException("Google 사용자 정보를 가져오는데 실패했습니다.")
                })
                .bodyToMono(GoogleUserInfo::class.java)
                .block()

        if (googleUserInfo == null) {
            throw OAuthException.OAuthRetrieveFailedException("Google 사용자 정보를 가져오는데 실패했습니다.")
        }

        return OAuthRetrieveHandler.OAuthResponse(
            username = googleUserInfo.name,
            socialId = googleUserInfo.id,
            email = googleUserInfo.email,
            profileImage = googleUserInfo.picture,
        )
    }

    data class GoogleUserInfo(
        val email: String,
        val name: String,
        val id: String,
        val picture: String,
    )
}
