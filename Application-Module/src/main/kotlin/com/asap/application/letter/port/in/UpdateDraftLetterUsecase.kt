package com.asap.application.letter.port.`in`

interface UpdateDraftLetterUsecase {
    fun command(command: Command)

    data class Command(
        val draftId: String,
        val userId: String,
        val content: String,
        val receiverName: String,
        val images: List<String>,
    )
}
