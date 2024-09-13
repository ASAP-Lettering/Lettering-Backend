package com.asap.bootstrap.letter.dto

import java.time.LocalDate

class AddPhysicalLetterRequest(
    val sender: String,
    val content: String,
    val images: List<String>,
    val receiveDate: LocalDate,
    val templateType: Int
) {
}