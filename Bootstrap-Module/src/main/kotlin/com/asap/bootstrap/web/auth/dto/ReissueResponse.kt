package com.asap.bootstrap.web.auth.dto

data class ReissueResponse(
    val accessToken: String,
    val refreshToken: String
) {
}