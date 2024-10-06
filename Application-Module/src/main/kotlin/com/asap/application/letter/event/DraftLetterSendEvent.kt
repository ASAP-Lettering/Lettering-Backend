package com.asap.application.letter.event

data class DraftLetterSendEvent(
    val draftLetterId: String,
    val userId: String,
)
