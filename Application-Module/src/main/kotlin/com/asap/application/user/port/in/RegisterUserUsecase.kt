package com.asap.application.user.port.`in`

import java.time.LocalDate

interface RegisterUserUsecase {
    fun registerUser(command: Command): Response

    data class Command(
        val registerToken: String,
        val servicePermission: Boolean,
        val privatePermission: Boolean,
        val marketingPermission: Boolean,
        val birthday: LocalDate?,
        val realName: String,
    )

    data class Response(
        val accessToken: String,
        val refreshToken: String,
        val userId: String,
    )
}
