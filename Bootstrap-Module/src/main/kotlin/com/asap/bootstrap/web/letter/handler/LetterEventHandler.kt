package com.asap.bootstrap.web.letter.handler

import com.asap.application.letter.port.`in`.RemoveLetterUsecase
import com.asap.application.user.event.UserEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterEventHandler(
    private val removeLetterUsecase: RemoveLetterUsecase,
) {
    @EventListener
    fun onUserDeleted(event: UserEvent.UserDeletedEvent) {
        removeLetterUsecase.removeAllIndependentLetterBy(
            RemoveLetterUsecase.Command.User(
                userId = event.user.id.value,
            ),
        )

        removeLetterUsecase.removeAllSenderLetterBy(
            RemoveLetterUsecase.Command.User(
                userId = event.user.id.value,
            ),
        )
    }
}
