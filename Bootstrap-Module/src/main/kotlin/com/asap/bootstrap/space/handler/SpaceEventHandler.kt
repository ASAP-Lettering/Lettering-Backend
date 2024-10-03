package com.asap.bootstrap.space.handler

import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.user.event.UserEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceEventHandler(
    private val spaceCreateUsecase: SpaceCreateUsecase,
) {
    @EventListener
    fun initializeSpace(event: UserEvent.UserCreatedEvent) {
        spaceCreateUsecase.create(
            command =
                SpaceCreateUsecase.Command(
                    userId = event.user.id.value,
                    spaceName = "${event.user.username}의 첫 행성",
                    templateType = 0,
                ),
        )
    }
}
