package com.asap.application.letter.port.`in`

interface AddLetterUsecase {
    fun addVerifiedLetter(command: Command.VerifyLetter)

    fun addPhysicalLetter(command: Command.AddPhysicalLetter)

    fun addAnonymousLetter(command: Command.AddAnonymousLetter)

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
            val draftId: String?,
        ) : Command()

        data class AddAnonymousLetter(
            val letterCode: String,
            val userId: String,
        ) : Command()
    }
}
