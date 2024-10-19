package com.asap.application.letter.port.`in`

interface RemoveLetterUsecase {
    fun removeSpaceLetter(command: Command.SpaceLetter)

    fun removeSpaceLetterBy(command: Command.SpaceId)

    fun removeSenderLetterBy(command: Command.SendLetter)

    fun removeIndependentLetter(command: Command.IndependentLetter)

    fun removeAllIndependentLetterBy(command: Command.User)

    fun removeAllSenderLetterBy(command: Command.User)

    fun removeAllSenderLetterBy(command: Command.SendLetters)

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

        data class User(
            val userId: String,
        ) : Command()

        data class SendLetter(
            val letterId: String,
            val userId: String,
        ) : Command()

        data class SendLetters(
            val letterIds: List<String>,
            val userId: String,
        ) : Command()
    }
}
