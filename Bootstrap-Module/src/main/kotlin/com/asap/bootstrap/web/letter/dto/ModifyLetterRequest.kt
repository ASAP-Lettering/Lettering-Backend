package com.asap.bootstrap.web.letter.dto

data class ModifyLetterRequest(
    val senderName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
)
