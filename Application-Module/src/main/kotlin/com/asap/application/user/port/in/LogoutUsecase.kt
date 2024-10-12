package com.asap.application.user.port.`in`

interface LogoutUsecase {
    fun logout(command: Command)

    data class Command(
        val refreshToken: String,
        val userId: String,
    )
}
