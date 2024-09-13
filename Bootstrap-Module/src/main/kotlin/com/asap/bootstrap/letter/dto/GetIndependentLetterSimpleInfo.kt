package com.asap.bootstrap.letter.dto

data class GetIndependentLetterSimpleInfo(
    val letterId: String,
    val senderName: String,
    val isNew: Boolean
) {
}