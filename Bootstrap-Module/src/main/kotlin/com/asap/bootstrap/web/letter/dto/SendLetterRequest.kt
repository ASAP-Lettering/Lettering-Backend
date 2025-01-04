package com.asap.bootstrap.web.letter.dto

data class SendLetterRequest(
    val receiverName:String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val draftId: String?
) {
}