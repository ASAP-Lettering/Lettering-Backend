package com.asap.application.user.port.`in`

import java.time.LocalDate

interface UpdateUserUsecase {
    fun executeFor(command: Command.Birthday)

    fun executeFor(command: Command.Onboarding)

    sealed class Command {
        data class Birthday(
            val userId: String,
            val birthday: LocalDate,
        ) : Command()

        data class Onboarding(
            val userId: String,
        ) : Command()
    }
}
