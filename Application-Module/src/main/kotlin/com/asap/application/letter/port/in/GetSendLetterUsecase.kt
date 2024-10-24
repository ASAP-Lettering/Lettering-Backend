package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetSendLetterUsecase {
    fun getHistory(query: Query.AllHistory): List<Response.History>

    fun getDetail(query: Query.Detail): Response.Detail

    sealed class Query {
        data class AllHistory(
            val userId: String,
        ) : Query()

        data class Detail(
            val userId: String,
            val letterId: String,
        ) : Query()
    }

    sealed class Response {
        data class History(
            val letterId: String,
            val receiverName: String,
            val sendDate: LocalDate,
        ) : Response()

        data class Detail(
            val receiverName: String,
            val sendDate: LocalDate,
            val content: String,
            val images: List<String>,
            val templateType: Int,
            val letterCode: String?,
        ) : Response()
    }
}
