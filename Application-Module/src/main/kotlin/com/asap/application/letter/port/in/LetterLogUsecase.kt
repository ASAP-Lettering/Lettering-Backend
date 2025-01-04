package com.asap.application.letter.port.`in`

import com.asap.domain.letter.entity.LetterLogType

interface LetterLogUsecase {

    fun log(request: LogRequest)

    data class LogRequest(
        val letterCode: String,
        val logType: LetterLogType,
        val logContent: String,
    )
}