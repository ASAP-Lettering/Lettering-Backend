package com.asap.application.space.port.`in`

interface SpaceUpdateNameUsecase {

    fun update(command: Command)

    data class Command(
        val userId: String,
        val spaceId: String,
        val name: String
    )

}