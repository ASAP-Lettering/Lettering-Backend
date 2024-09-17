package com.asap.bootstrap.letter.dto

import java.time.LocalDate

data class GetLetterDetailResponse(
    val senderName: String,
    val spaceName: String,
    val letterCount: Long,
    val content: String,
    val sendDate: LocalDate,
    val images: List<String>,
    val templateType: Int,
    val prevLetter: NearbyLetter?,
    val nextLetter: NearbyLetter?
) {


    data class NearbyLetter(
        val letterId: String,
        val senderName: String
    )
}