package com.asap.bootstrap.web.letter.dto

data class GetPhysicalDraftLetterResponse(
    val draftKey: String,
    val senderName: String,
    val content: String,
    val images: List<String>,
) {
}