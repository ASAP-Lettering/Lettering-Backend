package com.asap.application.letter.port.`in`

interface UpdateDraftLetterUsecase {
    fun command(command: Command.Send)

    fun command(command: Command.Physical)

    sealed class Command {
        data class Send(
            val draftId: String,
            val userId: String,
            val content: String,
            val images: List<String>,
            val receiverName: String,
        ) : Command()

        data class Physical(
            val draftId: String,
            val userId: String,
            val content: String,
            val images: List<String>,
            val senderName: String,
        ) : Command()
    }
}
