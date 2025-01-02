package com.asap.bootstrap.web.letter.dto

data class GetIndependentLetterSimpleInfo(
    val letterId: String,
    val senderName: String,
    val isNew: Boolean
) {
}