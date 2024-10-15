package com.asap.application.space.port.`in`

interface UpdateSpaceNameUsecase {
    fun update(command: Command)

    data class Command(
        val userId: String,
        val spaceId: String,
        val name: String,
    )
}
