package com.asap.application.user.port.`in`

interface SocialLoginUsecase {

    fun login(command: Command): Response

    data class Command(
        val provider: String,
        val accessToken: String,
    )

    sealed class Response {
    }

    data class Success(
        val accessToken: String,
        val refreshToken: String,
        val isProcessedOnboarding: Boolean
    ) : Response()

    data class NonRegistered(
        val registerToken: String
    ) : Response()
}