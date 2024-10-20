package com.asap.domain.space.event

import com.asap.domain.common.DomainEvent
import com.asap.domain.space.entity.Space

sealed class SpaceEvent : DomainEvent<Space> {
    data class SpaceCreatedEvent(
        val space: Space,
    ) : SpaceEvent()

    data class SpaceDeletedEvent(
        val space: Space,
    ) : SpaceEvent()
}
