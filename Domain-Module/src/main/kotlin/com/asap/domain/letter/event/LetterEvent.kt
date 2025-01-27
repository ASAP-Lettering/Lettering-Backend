package com.asap.domain.letter.event

import com.asap.domain.common.DomainEvent
import com.asap.domain.letter.entity.IndependentLetter
import com.asap.domain.letter.entity.SendLetter

sealed class SendLetterEvent : DomainEvent<SendLetter> {

    data class SendLetterCreatedEvent(
        val sendLetter: SendLetter,
        val draftId: String?,
    ) : SendLetterEvent()
}

sealed class IndependentLetterEvent : DomainEvent<IndependentLetter> {

    data class IndependentLetterCreatedEvent(
        val independentLetter: IndependentLetter,
        val receiveDraftId: String?,
    ) : IndependentLetterEvent()
}
