package com.asap.bootstrap.web.letter.dto

import java.time.LocalDate

data class GetSpaceLettersResponse(
    val senderName: String,
    val letterId: String,
    val receivedDate: LocalDate,
)
