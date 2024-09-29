package com.asap.application.letter.port.`in`

interface AddLetterUsecase {
    fun addVerifiedLetter(command: Command.VerifyLetter)

    fun addPhysicalLetter(command: Command.AddPhysicalLetter)

    sealed class Command {
        data class VerifyLetter(
            val letterId: String,
            val userId: String,
        ) : Command()

        data class AddPhysicalLetter(
            val senderName: String,
            val content: String,
            val images: List<String>,
            val templateType: Int,
            val userId: String,
        ) : Command()
    }
}
