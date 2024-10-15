package com.asap.bootstrap.letter.handler

import com.asap.application.letter.port.`in`.RemoveLetterUsecase
import com.asap.application.space.event.SpaceDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceLetterHandler(
    private val removeLetterUsecase: RemoveLetterUsecase,
) {
    @EventListener
    fun deleteBySpace(event: SpaceDeletedEvent) {
        removeLetterUsecase.removeSpaceLetterBy(
            command =
                RemoveLetterUsecase.Command.SpaceId(
                    userId = event.userId,
                    spaceId = event.spaceId,
                ),
        )
    }
}
