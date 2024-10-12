package com.asap.application.letter.port.`in`

interface GetAllLetterCountUsecase {
    fun get(query: Query): Response

    data class Query(
        val userId: String,
    )

    data class Response(
        val count: Long,
    )
}
