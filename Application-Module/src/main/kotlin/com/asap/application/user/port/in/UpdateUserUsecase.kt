package com.asap.application.user.port.`in`

import java.time.LocalDate

interface UpdateUserUsecase {
    fun executeFor(command: Command.Birthday)

    sealed class Command {
        data class Birthday(
            val userId: String,
            val birthday: LocalDate,
        ) : Command()
    }
}
