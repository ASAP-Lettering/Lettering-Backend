package com.asap.application.letter.port.`in`

interface GenerateDraftKeyUsecase {
    fun command(command: Command.Send): Response

    fun command(command: Command.Physical): Response

    sealed class Command {
        data class Send(
            val userId: String,
        ): Command()

        data class Physical(
            val userId: String,
        ): Command()
    }

    data class Response(
        val draftId: String,
    )
}
