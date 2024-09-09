package com.asap.application.space.port.`in`

interface SpaceUpdateIndexUsecase {

    fun update(command: Command)

    data class Command(
        val userId: String,
        val orders: List<SpaceOrder>
    ) {

        data class SpaceOrder(
            val spaceId: String,
            val index: Int
        )
    }
}