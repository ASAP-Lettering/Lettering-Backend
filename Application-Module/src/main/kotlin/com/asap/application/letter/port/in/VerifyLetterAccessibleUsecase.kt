package com.asap.application.letter.port.`in`

interface VerifyLetterAccessibleUsecase {

    fun verify(
        command: Command
    ): Response


    data class Command(
        val letterCode: String,
        val userId: String
    )

    data class Response(
        val letterId: String
    )
}