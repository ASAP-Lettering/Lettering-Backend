package com.asap.bootstrap.letter.dto

data class UpdateDraftLetterRequest(
    val content: String,
    val receiverName: String,
    val images: List<String>,
)
