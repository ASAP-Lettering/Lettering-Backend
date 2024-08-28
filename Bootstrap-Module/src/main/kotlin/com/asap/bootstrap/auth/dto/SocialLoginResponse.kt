package com.asap.bootstrap.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = "소셜 로그인 응답",
    oneOf = [
        SocialLoginResponse.Success::class,
        SocialLoginResponse.NonRegistered::class
    ]
)
sealed class SocialLoginResponse{

    @Schema(description = "기존 회원 로그인 성공")
    data class Success(
        @Schema(description = "access token")
        val accessToken: String,
        @Schema(description = "refresh token")
        val refreshToken: String
    ) : SocialLoginResponse()


    @Schema(description = "신규 회원 가입 필요")
    data class NonRegistered(
        @Schema(description = "register token, 회원가입을 위한 토큰")
        val registerToken: String
    ) : SocialLoginResponse()
}
