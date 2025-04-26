package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NaverOAuthRetrieveHandler(
    @Qualifier("naverWebClient") naverWebClient: WebClient,
) : AbstractOAuthRetrieveHandler<NaverOAuthRetrieveHandler.NaverApiResponse>(naverWebClient) {

    override fun getApiEndpoint(): String = "/v1/nid/me"

    override fun getErrorMessage(): String = "네이버 사용자 정보를 가져오는데 실패했습니다."

    override fun getResponseType(): Class<NaverApiResponse> = NaverApiResponse::class.java

    override fun mapToOAuthResponse(response: NaverApiResponse): OAuthRetrieveHandler.OAuthResponse {
        return OAuthRetrieveHandler.OAuthResponse(
            username = response.response.nickname,
            socialId = response.response.id,
            email = response.response.email,
            profileImage = response.response.profile_image,
        )
    }

    data class NaverApiResponse(
        val resultcode: String,
        val message: String,
        val response: NaverUserResponse,
    )

    data class NaverUserResponse(
        val id: String,
        val nickname: String,
        val name: String,
        val email: String,
        val gender: String,
        val age: String,
        val birthday: String,
        val profile_image: String,
        val birthyear: String,
        val mobile: String,
    )
}
