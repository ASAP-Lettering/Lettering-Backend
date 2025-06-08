package com.asap.bootstrap.web.letter.dto

data class AnonymousSendLetterRequest(
    val senderName: String? = null,
    val receiverName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
)
