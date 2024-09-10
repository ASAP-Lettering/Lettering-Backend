package com.asap.bootstrap.auth.dto

data class ReissueResponse(
    val accessToken: String,
    val refreshToken: String
) {
}