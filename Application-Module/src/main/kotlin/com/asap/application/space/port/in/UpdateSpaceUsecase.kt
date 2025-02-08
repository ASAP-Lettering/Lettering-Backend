package com.asap.application.space.port.`in`

interface UpdateSpaceUsecase {
    fun update(command: Command.Index)

    fun update(command: Command.Main)


    sealed class Command {
        data class Index(
            val userId: String,
            val orders: List<SpaceOrder>,
        ) : Command()

        data class SpaceOrder(
            val spaceId: String,
            val index: Int,
        )

        data class Main(
            val userId: String,
            val spaceId: String,
        ) : Command()
    }
}
