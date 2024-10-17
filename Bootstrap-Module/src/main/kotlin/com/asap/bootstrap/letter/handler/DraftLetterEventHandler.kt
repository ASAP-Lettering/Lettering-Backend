package com.asap.bootstrap.letter.handler

import com.asap.application.letter.event.DraftLetterSendEvent
import com.asap.application.letter.port.`in`.RemoveDraftLetterUsecase
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class DraftLetterEventHandler(
    private val removeDraftLetterUsecase: RemoveDraftLetterUsecase,
) {
    @EventListener
    fun deleteDraftLetter(event: DraftLetterSendEvent) {
        removeDraftLetterUsecase.command(
            RemoveDraftLetterUsecase.Command(
                userId = event.userId,
                draftId = event.draftLetterId,
            ),
        )
    }
}
