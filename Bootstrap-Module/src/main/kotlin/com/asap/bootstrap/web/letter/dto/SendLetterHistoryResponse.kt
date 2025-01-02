package com.asap.bootstrap.web.letter.dto

import java.time.LocalDate

data class SendLetterHistoryResponse(
    val letterId: String,
    val receiverName: String,
    val sendDate: LocalDate,
)
