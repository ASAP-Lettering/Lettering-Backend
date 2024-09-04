package com.asap.application.space.port.`in`

interface MainSpaceQueryUsecase {

    fun query(): Response

    data class Response(
        val id: String
    )

}