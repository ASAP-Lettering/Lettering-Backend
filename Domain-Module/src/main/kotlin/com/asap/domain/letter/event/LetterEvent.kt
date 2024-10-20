package com.asap.domain.letter.event

import com.asap.domain.common.DomainEvent
import com.asap.domain.letter.entity.SendLetter

sealed class SendLetterEvent : DomainEvent<SendLetter> {

    data class SendLetterCreatedEvent(
        val sendLetter: SendLetter,
        val draftId: String?,
    ) : SendLetterEvent()
}
