package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetIndependentLettersUsecase {
    fun getAll(queryAll: QueryAll): Response.All

    fun get(query: Query): Response.One

    data class QueryAll(
        val userId: String,
    )

    data class Query(
        val userId: String,
        val letterId: String,
    )

    sealed class Response {
        data class All(
            val letters: List<LetterInfo>,
        ) : Response()

        data class One(
            val senderName: String,
            val letterCount: Long,
            val content: String,
            val sendDate: LocalDate,
            val images: List<String>,
            val templateType: Int,
            val prevLetter: NearbyLetter?,
            val nextLetter: NearbyLetter?,
        ) : Response()
    }

    data class NearbyLetter(
        val letterId: String,
        val senderName: String,
    )

    data class LetterInfo(
        val letterId: String,
        val senderName: String,
        val isNew: Boolean,
    )
}
