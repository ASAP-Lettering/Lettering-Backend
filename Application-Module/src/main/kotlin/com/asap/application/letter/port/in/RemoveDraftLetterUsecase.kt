package com.asap.application.letter.port.`in`

interface RemoveDraftLetterUsecase {
    fun deleteBy(command: Command.Draft)

    fun deleteBy(command: Command.User)

//    data class Command(
//        val draftId: String,
//        val userId: String,
//    )

    sealed class Command  {
        data class User(
            val userId: String,
        ) : Command()

        data class Draft(
            val draftId: String,
            val userId: String,
        ) : Command()
    }
}
