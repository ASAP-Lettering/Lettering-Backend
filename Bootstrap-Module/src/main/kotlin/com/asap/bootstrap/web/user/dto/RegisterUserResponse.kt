package com.asap.bootstrap.web.user.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원 가입 응답")
data class RegisterUserResponse(
    @Schema(description = "access token")
    val accessToken: String,
    @Schema(description = "refresh token")
    val refreshToken: String
) {
}