package com.asap.bootstrap.letter.dto


data class LetterVerifyRequest(
    val letterCode: String,
    val receiverName: String
) {
}