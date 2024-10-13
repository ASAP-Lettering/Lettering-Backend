package com.asap.application.letter.port.`in`

import java.time.LocalDate

interface GetSendLetterUsecase {
    fun getHistory(query: Query.AllHistory): List<Response.History>

    sealed class Query  {

        data class AllHistory(
            val userId: String,
        ) : Query()
    }

    sealed class Response  {

        data class History(
            val letterId: String,
            val receiverName: String,
            val sendDate: LocalDate,
        ) : Response()
    }
}
