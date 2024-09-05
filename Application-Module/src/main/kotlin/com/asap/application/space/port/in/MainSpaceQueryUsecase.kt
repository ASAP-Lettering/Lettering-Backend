package com.asap.application.space.port.`in`

interface MainSpaceQueryUsecase {

    fun get(query: Query): Response

    data class Query(
        val userId: String
    )

    data class Response(
        val id: String
    )

}