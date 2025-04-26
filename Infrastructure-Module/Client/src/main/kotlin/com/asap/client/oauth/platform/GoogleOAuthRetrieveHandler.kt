package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleOAuthRetrieveHandler(
    @Qualifier("googleWebClient") googleWebClient: WebClient,
) : AbstractOAuthRetrieveHandler<GoogleOAuthRetrieveHandler.GoogleUserInfo>(googleWebClient) {

    override fun getApiEndpoint(): String = "/userinfo/v2/me"

    override fun getErrorMessage(): String = "Google 사용자 정보를 가져오는데 실패했습니다."

    override fun getResponseType(): Class<GoogleUserInfo> = GoogleUserInfo::class.java

    override fun mapToOAuthResponse(response: GoogleUserInfo): OAuthRetrieveHandler.OAuthResponse {
        return OAuthRetrieveHandler.OAuthResponse(
            username = response.name,
            socialId = response.id,
            email = response.email,
            profileImage = response.picture,
        )
    }

    data class GoogleUserInfo(
        val email: String,
        val name: String,
        val id: String,
        val picture: String,
    )
}
