package com.asap.application.letter.port.`in`

interface RemoveLetterUsecase {
    fun removeSpaceLetter(command: Command.SpaceLetter)

    fun removeSpaceLetterBy(command: Command.SpaceId)

    fun removeIndependentLetter(command: Command.IndependentLetter)

    sealed class Command {
        data class SpaceLetter(
            val letterId: String,
            val userId: String,
        ) : Command()

        data class SpaceId(
            val spaceId: String,
            val userId: String,
        ) : Command()

        data class IndependentLetter(
            val letterId: String,
            val userId: String,
        ) : Command()
    }
}
