package com.asap.application.letter.port.`in`

import java.time.LocalDateTime

interface GetPhysicalDraftLetterUsecase {

    fun getAll(query: Query.All): Response.All

    fun getByKey(query: Query.ByKey): Response.ByKey

    sealed class Query {
        data class All(
            val userId: String,
        ) : Query()

        data class ByKey(
            val draftKey: String,
            val userId: String,
        ) : Query()
    }

    sealed class Response {
        data class All(
            val drafts: List<ByKey>,
        ) : Response()

        data class ByKey(
            val draftKey: String,
            val senderName: String,
            val content: String,
            val images: List<String>,
            val lastUpdated: LocalDateTime,
        ) : Response()
    }
}