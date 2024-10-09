package com.asap.application.letter.port.`in`

interface UpdateLetterUsecase {
    fun updateIndependentLetter(command: Command.Independent)

    fun updateSpaceLetter(command: Command.Space)

    sealed class Command  {

        data class Independent(
            val letterId: String,
            val userId: String,
            val senderName: String,
            val content: String,
            val images: List<String>,
        ) : Command()

        data class Space(
            val letterId: String,
            val userId: String,
            val senderName: String,
            val content: String,
            val images: List<String>,
        ) : Command()
    }
}
