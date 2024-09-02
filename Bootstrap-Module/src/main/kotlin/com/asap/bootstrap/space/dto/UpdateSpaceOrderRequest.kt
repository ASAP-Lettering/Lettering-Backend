package com.asap.bootstrap.space.dto

data class UpdateSpaceOrderRequest(
    val orders: List<SpaceOrder>
) {

    data class SpaceOrder(
        val spaceId: String,
        val index: Int
    )
}