package com.asap.client.oauth.platform

import com.asap.client.ClientProperties
import com.asap.client.oauth.OAuthRetrieveHandler
import com.asap.client.oauth.exception.OAuthException
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@Component
class NaverOAuthRetrieveHandler(
    @Qualifier("naverWebClient") naverWebClient: WebClient,
    private val clientProperties: ClientProperties,
) : AbstractOAuthRetrieveHandler<NaverOAuthRetrieveHandler.NaverOAuthUserInfoResponse>(naverWebClient) {
    private val naverOAuthConfig by lazy { clientProperties.oauth.naver }

    override fun getAccessToken(request: OAuthRetrieveHandler.OAuthGetAccessTokenRequest): OAuthRetrieveHandler.OAuthAccessTokenResponse {
        val accessTokenUri =
            NaverAccessTokenRequest(
                grantType = "authorization_code",
                clientId = naverOAuthConfig.clientId,
                clientSecret = naverOAuthConfig.clientSecret,
                code = request.code,
            ).toUriComponents("/oauth2/token")

        val response =
            webClient
                .get()
                .uri(accessTokenUri)
                .retrieve()
                .bodyToMono(NaverAccessTokenResponse::class.java)
                .block()
                ?: throw OAuthException.OAuthRetrieveFailedException(getErrorMessage())

        return OAuthRetrieveHandler.OAuthAccessTokenResponse(response.accessToken, response.tokenType)
    }

    override fun getApiEndpoint(): String = "/v1/nid/me"

    override fun getErrorMessage(): String = "네이버 사용자 정보를 가져오는데 실패했습니다."

    override fun getResponseType(): Class<NaverOAuthUserInfoResponse> = NaverOAuthUserInfoResponse::class.java

    override fun mapToOAuthResponse(response: NaverOAuthUserInfoResponse): OAuthRetrieveHandler.OAuthResponse =
        OAuthRetrieveHandler.OAuthResponse(
            username = response.response.nickname,
            socialId = response.response.id,
            email = response.response.email,
            profileImage = response.response.profile_image,
        )

    data class NaverOAuthUserInfoResponse(
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

    data class NaverAccessTokenRequest(
        @JsonProperty("grant_type")
        val grantType: String,
        @JsonProperty("client_id")
        val clientId: String,
        @JsonProperty("client_secret")
        val clientSecret: String,
        val code: String,
    ) {
        fun toUriComponents(baseUri: String): String =
            UriComponentsBuilder
                .fromPath(baseUri)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .toUriString()
    }

    data class NaverAccessTokenResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val expiresIn: Int,
    )
}
