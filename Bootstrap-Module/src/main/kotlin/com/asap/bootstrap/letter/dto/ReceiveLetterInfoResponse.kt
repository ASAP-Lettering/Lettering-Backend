package com.asap.bootstrap.letter.dto

import java.time.LocalDate

data class ReceiveLetterInfoResponse(
    val senderName: String,
    val content: String,
    val date: LocalDate,
    val templateType: Int
) {
}