package com.asap.application.letter.port.`in`

interface RemoveDraftLetterUsecase {
    fun deleteBy(command: Command.Send)

    fun deleteBy(command: Command.User)

    fun deleteBy(command: Command.Physical)

    sealed class Command  {
        data class User(
            val userId: String,
        ) : Command()

        data class Send(
            val draftId: String,
            val userId: String,
        ) : Command()

        data class Physical(
            val draftId: String,
            val userId: String,
        ) : Command()
    }
}
