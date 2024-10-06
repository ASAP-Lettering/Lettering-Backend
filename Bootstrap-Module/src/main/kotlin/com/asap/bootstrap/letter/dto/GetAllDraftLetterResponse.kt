package com.asap.bootstrap.letter.dto

import java.time.LocalDateTime

data class GetAllDraftLetterResponse(
    val drafts: List<DraftLetterInfo>,
)

data class DraftLetterInfo(
    val draftKey: String,
    val receiverName: String,
    val content: String,
    val lastUpdated: LocalDateTime,
)
