package com.asap.bootstrap.letter.dto

data class ModifyLetterRequest(
    val senderName: String,
    val content: String,
    val images: List<String>
) {
}