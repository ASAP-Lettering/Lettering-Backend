package com.asap.application.user.event

import com.asap.domain.user.entity.User

sealed class UserEvent {
    data class UserCreatedEvent(
        val user: User,
    )

    data class UserDeletedEvent(
        val user: User,
    )
}
