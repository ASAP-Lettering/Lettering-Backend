package com.asap.application.space.port.`in`

interface MainSpaceGetUsecase {
    fun get(query: Query): Response

    data class Query(
        val userId: String,
    )

    data class Response(
        val id: String,
        val username: String,
        val templateType: Int,
        val spaceName: String,
    )
}
