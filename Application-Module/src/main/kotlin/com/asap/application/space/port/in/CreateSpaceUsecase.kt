package com.asap.application.space.port.`in`

interface CreateSpaceUsecase {
    fun create(command: Command)

    data class Command(
        val userId: String,
        val spaceName: String,
        val templateType: Int,
    )
}
