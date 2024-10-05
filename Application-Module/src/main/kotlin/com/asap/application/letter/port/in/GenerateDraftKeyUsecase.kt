package com.asap.application.letter.port.`in`

interface GenerateDraftKeyUsecase {
    fun command(command: Command): Response

    data class Command(
        val userId: String,
    )

    data class Response(
        val draftId: String,
    )
}
