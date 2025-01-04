package com.asap.application.letter.port.`in`

import com.asap.domain.letter.entity.LetterLogType

interface LetterLogUsecase {

    fun log(request: LogRequest)

    fun finLatestLogByLetterCode(letterCode: String): LogResponse?

    data class LogRequest(
        val letterCode: String,
        val logType: LetterLogType,
        val logContent: String,
    )

    data class LogResponse(
        val letterId: String,
        val logType: LetterLogType,
        val logContent: String,
    )
}