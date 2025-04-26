package com.asap.client.oauth.platform

import com.asap.client.oauth.OAuthRetrieveHandler
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoOAuthRetrieveHandler(
    @Qualifier("kakaoWebClient") kakaoWebClient: WebClient,
) : AbstractOAuthRetrieveHandler<KakaoOAuthRetrieveHandler.KakaoUserInfo>(kakaoWebClient) {

    override fun getApiEndpoint(): String = "/v2/user/me"

    override fun getErrorMessage(): String = "Kakao 사용자 정보를 가져오는데 실패했습니다."

    override fun getResponseType(): Class<KakaoUserInfo> = KakaoUserInfo::class.java

    override fun mapToOAuthResponse(response: KakaoUserInfo): OAuthRetrieveHandler.OAuthResponse {
        return OAuthRetrieveHandler.OAuthResponse(
            username = response.properties.nickname,
            socialId = response.id,
            profileImage = response.properties.profileImage,
            email = response.kakaoAccount.email,
        )
    }

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
