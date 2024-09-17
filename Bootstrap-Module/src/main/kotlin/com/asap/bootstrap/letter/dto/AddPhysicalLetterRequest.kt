package com.asap.bootstrap.letter.dto

class AddPhysicalLetterRequest(
    val senderName: String,
    val content: String,
    val images: List<String>,
    val templateType: Int
) {
}