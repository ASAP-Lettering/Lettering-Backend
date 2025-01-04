package com.asap.bootstrap.web.letter.dto

import java.time.LocalDate

data class SendLetterDetailResponse(
    val receiverName: String,
    val sendDate: LocalDate,
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val letterCode: String?,
)
