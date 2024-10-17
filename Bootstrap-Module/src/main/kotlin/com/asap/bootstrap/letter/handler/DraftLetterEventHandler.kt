package com.asap.bootstrap.letter.handler

import com.asap.application.letter.event.DraftLetterSendEvent
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import com.asap.application.user.event.UserEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterEventHandler(
    private val removeDraftLetterUsecase: RemoveDraftLetterUsecase,
) {
    @EventListener
    fun deleteDraftLetter(event: DraftLetterSendEvent) {
        removeDraftLetterUsecase.deleteBy(
            RemoveDraftLetterUsecase.Command.Draft(
                userId = event.userId,
                draftId = event.draftLetterId,
            ),
        )
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
