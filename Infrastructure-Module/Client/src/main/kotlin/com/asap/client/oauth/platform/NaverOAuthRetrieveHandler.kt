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
    @Qualifier("getNaverAccessTokenWebClient") val getNaverAccessTokenWebClient: WebClient,
    private val clientProperties: ClientProperties,
) : AbstractOAuthRetrieveHandler<NaverOAuthRetrieveHandler.NaverOAuthUserInfoResponse>(naverWebClient) {
    private val naverOAuthConfig by lazy { clientProperties.oauth.naver }

    override fun getAccessToken(request: OAuthRetrieveHandler.OAuthGetAccessTokenRequest): OAuthRetrieveHandler.OAuthAccessTokenResponse {
        val accessTokenUrl =
            NaverAccessTokenRequest(
                grantType = "authorization_code",
                clientId = naverOAuthConfig.clientId,
                clientSecret = naverOAuthConfig.clientSecret,
                code = request.code,
            ).toUriComponents("/oauth2.0/token")

        val response =
            getNaverAccessTokenWebClient
                .get()
                .uri(accessTokenUrl)
                .retrieve()
                .bodyToMono(NaverAccessTokenResponse::class.java)
                .block()
                ?: throw OAuthException.OAuthRetrieveFailedException(getErrorMessage())

        return OAuthRetrieveHandler.OAuthAccessTokenResponse(
            accessToken = response.accessToken,
            refreshToken = response.tokenType,
        )
    }

    override fun getApiEndpoint(): String = "/v1/nid/me"

    override fun getErrorMessage(): String = "네이버 사용자 정보를 가져오는데 실패했습니다."

    override fun getResponseType(): Class<NaverOAuthUserInfoResponse> = NaverOAuthUserInfoResponse::class.java

    override fun mapToOAuthResponse(response: NaverOAuthUserInfoResponse): OAuthRetrieveHandler.OAuthResponse =
        OAuthRetrieveHandler.OAuthResponse(
            username = "",
            socialId = response.response.id,
            email = response.response.email,
            profileImage = "",
        )

    data class NaverOAuthUserInfoResponse(
        val resultcode: String,
        val message: String,
        val response: NaverUserResponse,
    )

    data class NaverUserResponse(
        val id: String,
        val email: String,
    )

    data class NaverAccessTokenRequest(
        val grantType: String,
        val clientId: String,
        val clientSecret: String,
        val code: String,
    ) {
        fun toUriComponents(basePath: String): String =
            UriComponentsBuilder
                .fromPath(basePath)
                .queryParam("grant_type", grantType)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .toUriString()
    }

    data class NaverAccessTokenResponse(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("refresh_token")
        val refreshToken: String,
        @JsonProperty("token_type")
        val tokenType: String,
        @JsonProperty("expires_in")
        val expiresIn: Int,
    )
}
