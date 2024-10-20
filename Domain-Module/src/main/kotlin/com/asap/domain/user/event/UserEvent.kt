package com.asap.application.user.event

import com.asap.domain.common.DomainEvent
import com.asap.domain.user.entity.User

sealed class UserEvent : DomainEvent<User> {
    data class UserCreatedEvent(
        val user: User,
    ) : UserEvent()

    data class UserDeletedEvent(
        val user: User,
    ) : UserEvent()
}
