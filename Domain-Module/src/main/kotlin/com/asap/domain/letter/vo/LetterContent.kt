package com.asap.domain.letter.vo

data class LetterContent(
    val content: String,
    val images: List<String>,
    val templateType: Int
) {
}