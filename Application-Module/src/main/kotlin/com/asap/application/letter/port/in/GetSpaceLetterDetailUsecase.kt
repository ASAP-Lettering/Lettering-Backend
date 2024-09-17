package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetSpaceLetterDetailUsecase {

    fun get(query: Query): Response

    data class Query(
        val letterId: String,
        val userId: String
    )


    data class Response(
        val senderName: String,
        val spaceName: String,
        val letterCount: Long,
        val content: String,
        val sendDate: LocalDate,
        val images: List<String>,
        val templateType: Int,
        val prevLetter: NearbyLetter?,
        val nextLetter: NearbyLetter?
    )

    data class NearbyLetter(
        val letterId: String,
        val senderName: String
    )
}