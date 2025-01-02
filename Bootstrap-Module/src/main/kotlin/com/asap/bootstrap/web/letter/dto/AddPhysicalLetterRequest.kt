package com.asap.bootstrap.web.letter.dto

class AddPhysicalLetterRequest(
    val senderName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int
) {
}