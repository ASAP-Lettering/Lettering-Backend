package com.asap.application.letter.port.`in`

interface RemoveDraftLetterUsecase {
    fun command(command: Command)

    data class Command(
        val draftId: String,
        val userId: String,
    )
}
