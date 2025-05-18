package com.asap.bootstrap.web.letter.handler

import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.user.event.UserEvent
import com.asap.domain.letter.event.IndependentLetterEvent
import com.asap.domain.letter.event.SendLetterEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterEventHandler(
    private val removeDraftLetterUsecase: RemoveDraftLetterUsecase,
) {
    @EventListener
    fun deleteDraftLetter(event: SendLetterEvent.SendLetterCreatedEvent) {
        event.draftId?.let {
            removeDraftLetterUsecase.deleteBy(
                RemoveDraftLetterUsecase.Command.Send(
                    userId = event.sendLetter.senderId!!.value,
                    draftId = it,
                ),
            )
        }
    }

    @EventListener
    fun deletePhysicalDraftLetter(event: IndependentLetterEvent.IndependentLetterCreatedEvent) {
        event.receiveDraftId?.let {
            removeDraftLetterUsecase.deleteBy(
                RemoveDraftLetterUsecase.Command.Physical(
                    userId = event.independentLetter.getOwnerId().value,
                    draftId = it,
                ),
            )
        }
    }

    @EventListener
    fun onUserDelete(event: UserEvent.UserDeletedEvent) {
        removeDraftLetterUsecase.deleteBy(
            RemoveDraftLetterUsecase.Command.User(
                userId = event.user.id.value,
            ),
        )
    }
}
