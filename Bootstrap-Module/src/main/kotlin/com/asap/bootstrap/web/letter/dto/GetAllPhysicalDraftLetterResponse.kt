package com.asap.bootstrap.web.letter.dto

import java.time.LocalDateTime

data class GetAllPhysicalDraftLetterResponse(
    val drafts: List<PhysicalDraftLetterInfo>,
) {
}

data class PhysicalDraftLetterInfo(
    val draftKey: String,
    val senderName: String,
    val content: String,
    val lastUpdated: LocalDateTime,
) {
}