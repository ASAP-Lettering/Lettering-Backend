package com.asap.bootstrap.letter.handler

import com.asap.application.letter.port.`in`.RemoveLetterUsecase
import com.asap.domain.space.event.SpaceEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceLetterEventHandler(
    private val removeLetterUsecase: RemoveLetterUsecase,
) {
    @EventListener
    fun deleteBySpace(event: SpaceEvent.SpaceDeletedEvent) {
        removeLetterUsecase.removeSpaceLetterBy(
            command =
                RemoveLetterUsecase.Command.SpaceId(
                    userId = event.space.userId.value,
                    spaceId = event.space.id.value,
                ),
        )
    }
}
