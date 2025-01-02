package com.asap.bootstrap.web.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 로그인 요청")
data class SocialLoginRequest(
    @Schema(description = "oauth access token")
    val accessToken: String,
)
