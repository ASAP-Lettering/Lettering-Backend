package com.asap.bootstrap.letter.dto

data class GetDraftLetterResponse(
    val draftKey: String,
    val receiverName: String,
    val content: String,
    val images: List<String>,
)
