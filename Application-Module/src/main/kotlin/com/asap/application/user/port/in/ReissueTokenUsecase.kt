package com.asap.application.user.port.`in`

interface ReissueTokenUsecase {

    fun reissue(request: Command): Response

    data class Command(
        val refreshToken: String
    )

    data class Response(
        val accessToken: String,
        val refreshToken: String
    )
}