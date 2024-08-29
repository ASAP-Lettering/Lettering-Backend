package com.asap.client.oauth

interface OAuthRetrieveHandler {

    fun getOAuthInfo(request: OAuthRequest): OAuthResponse

    data class OAuthRequest(
        val accessToken: String
    )


    data class OAuthResponse(
        val username: String,
        val socialId: String,
        val profileImage: String
    )
}