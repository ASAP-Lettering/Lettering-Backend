package com.asap.bootstrap.web.letter.dto

data class GetDraftLetterResponse(
    val draftKey: String,
    val receiverName: String,
    val content: String,
    val images: List<String>,
)
