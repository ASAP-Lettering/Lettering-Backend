package com.asap.domain.space.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.space.event.SpaceEvent

class Space(
    id: DomainId,
    val userId: DomainId,
    var name: String,
    val templateType: Int,
    var isMain: Boolean = false,
) : Aggregate<Space>(id) {
    companion object {
        fun create(
            id: DomainId = DomainId.generate(),
            userId: DomainId,
            name: String,
            templateType: Int,
        ): Space =
            Space(
                id = id,
                userId = userId,
                name = name,
                templateType = templateType,
            ).also {
                it.registerEvent(SpaceEvent.SpaceCreatedEvent(it))
            }
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun delete() {
        this.registerEvent(SpaceEvent.SpaceDeletedEvent(this))
    }
}
