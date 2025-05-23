package com.asap.bootstrap.web.space.handler

import com.asap.application.space.port.`in`.CreateSpaceUsecase
import com.asap.application.space.port.`in`.DeleteSpaceUsecase
import com.asap.application.user.event.UserEvent
import org.springframework.context.event.EventListener
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceEventHandler(
    private val spaceCreateUsecase: CreateSpaceUsecase,
    private val deleteSpaceUsecase: DeleteSpaceUsecase,
) {
    @EventListener
    fun initializeSpace(event: UserEvent.UserCreatedEvent) {
        spaceCreateUsecase.create(
            command =
                CreateSpaceUsecase.Command(
                    userId = event.user.id.value,
                    spaceName = "${event.user.username}의 첫 행성",
                    templateType = 0,
                ),
        )
    }

    @EventListener
    fun onUserDeleted(event: UserEvent.UserDeletedEvent) {
        deleteSpaceUsecase.deleteAllBy(
            command =
                DeleteSpaceUsecase.DeleteAllUser(
                    userId = event.user.id.value,
                ),
        )
    }
}
