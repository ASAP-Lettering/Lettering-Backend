package com.asap.bootstrap.web.letter.dto

data class UpdatePhysicalDraftLetterRequest(
    val senderName: String,
    val content: String,
    val images: List<String>
) {
}