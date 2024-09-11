package com.asap.application.letter.port.`in`

interface SendLetterUsecase {

    fun send(
        command: Command
    ): Response

    data class Command(
        val receiverName: String,
        val content: String,
        val images: List<String>,
        val templateType: Int,
        val draftId: String?,
        val userId: String
    )

    data class Response(
        val letterCode: String
    )
}