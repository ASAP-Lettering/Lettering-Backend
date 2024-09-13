package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetVerifiedLetterUsecase {

    fun get(
        query: Query
    ): Response

    data class Query(
        val letterId: String,
        val userId: String
    )

    data class Response(
        val senderName: String,
        val content: String,
        val sendDate: LocalDate,
        val templateType: Int,
        val images: List<String>
    )


}