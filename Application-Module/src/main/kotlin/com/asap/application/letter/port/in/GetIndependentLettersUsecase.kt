package com.asap.application.letter.port.`in`

interface GetIndependentLettersUsecase {

    fun get(query: Query): Response

    data class Query(
        val userId: String
    )

    data class Response(
        val letters: List<LetterInfo>
    )

    data class LetterInfo(
        val letterId: String,
        val senderName: String,
        val isNew: Boolean
    )
}