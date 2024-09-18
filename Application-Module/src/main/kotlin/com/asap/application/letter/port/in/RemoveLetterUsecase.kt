package com.asap.application.letter.port.`in`

interface RemoveLetterUsecase {

    fun removeSpaceLetter(command: Command.SpaceLetter)

    sealed class Command{
        data class SpaceLetter(
            val letterId: String,
            val userId: String
        ): Command()

    }
}