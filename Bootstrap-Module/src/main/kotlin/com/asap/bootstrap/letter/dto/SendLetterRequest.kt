package com.asap.bootstrap.letter.dto

data class SendLetterRequest(
    val receiverName:String,
    val content: String,
    val images: List<String>,
    val templateType: Int,
    val draftId: String?
) {
}