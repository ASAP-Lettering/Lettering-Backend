package com.asap.bootstrap.letter.dto

import org.springframework.web.multipart.MultipartRequest
import java.time.LocalDate

class AddDirectLetterRequest(
    val sender: String,
    val content: String,
    val images: List<MultipartRequest>,
    val receiveDate: LocalDate,
    val templateType: Int
) {
}