package com.asap.application.letter.port.`in`

interface MoveLetterUsecase {


    fun moveToSpace(
        command: Command.ToSpace
    )

    fun moveToIndependent(
        command: Command.ToIndependent
    )


    sealed class Command{
        data class ToSpace(
            val letterId: String,
            val spaceId: String,
            val userId: String
        ): Command()


        data class ToIndependent(
            val letterId: String,
            val userId: String
        ): Command()
    }
}