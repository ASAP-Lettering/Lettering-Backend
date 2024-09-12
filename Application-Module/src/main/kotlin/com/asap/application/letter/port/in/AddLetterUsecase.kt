package com.asap.application.letter.port.`in`

interface AddLetterUsecase {

    fun addVerifiedLetter(
        command: Command.VerifyLetter
    )

    sealed class Command {

        data class VerifyLetter(
            val letterId: String,
            val userId: String
        ): Command()
    }
}