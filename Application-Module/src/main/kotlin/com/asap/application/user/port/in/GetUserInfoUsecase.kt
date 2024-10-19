package com.asap.application.user.port.`in`

import java.time.LocalDate

interface GetUserInfoUsecase {
    fun getBy(query: Query.Me): Response

    sealed class Query  {
        data class Me(
            val userId: String,
        ) : Query()
    }

    data class Response(
        val name: String,
        val socialPlatform: String,
        val email: String,
        val birthday: LocalDate?,
    )
}
