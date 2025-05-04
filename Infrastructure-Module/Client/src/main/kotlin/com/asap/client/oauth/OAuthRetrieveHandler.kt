package com.asap.client.oauth

interface OAuthRetrieveHandler {
    fun getOAuthInfo(request: OAuthRequest): OAuthResponse

    fun getAccessToken(request: OAuthGetAccessTokenRequest): OAuthAccessTokenResponse =
        throw UnsupportedOperationException("This operation is not supported yet.")

    data class OAuthRequest(
        val accessToken: String,
    )

    data class OAuthResponse(
        val username: String,
        val socialId: String,
        val email: String,
        val profileImage: String,
    )

    data class OAuthGetAccessTokenRequest(
        val code: String,
    )

    data class OAuthAccessTokenResponse(
        val accessToken: String,
        val refreshToken: String?,
    )
}
