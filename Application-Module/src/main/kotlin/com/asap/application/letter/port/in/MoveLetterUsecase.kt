package com.asap.application.letter.port.`in`

interface MoveLetterUsecase {


    fun moveToSpace(
        command: Command.ToSpace
    )


    sealed class Command{
        data class ToSpace(
            val letterId: String,
            val spaceId: String,
            val userId: String
        ): Command()

    }
}