package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetSpaceLettersUsecase {
    fun get(query: Query): Response

    data class Query(
        val page: Int,
        val size: Int,
        val spaceId: String,
        val userId: String,
    )

    data class Response(
        val letters: List<LetterInfo>,
        val total: Long,
        val page: Int,
        val size: Int,
        val totalPages: Int,
    )

    data class LetterInfo(
        val senderName: String,
        val letterId: String,
        val receivedDate: LocalDate,
    )
}
