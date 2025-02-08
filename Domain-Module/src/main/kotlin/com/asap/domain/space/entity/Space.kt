package com.asap.domain.space.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.space.event.SpaceEvent

class Space(
    id: DomainId,
    val userId: DomainId,
    var name: String,
    var index: Int,
    val templateType: Int,
    var isMain: Boolean = false,
) : Aggregate<Space>(id) {
    companion object {
        fun create(
            id: DomainId = DomainId.generate(),
            userId: DomainId,
            name: String,
            templateType: Int,
            index: Int = -1,
        ): Space =
            Space(
                id = id,
                userId = userId,
                name = name,
                templateType = templateType,
                index = index,
            ).also {
                it.registerEvent(SpaceEvent.SpaceCreatedEvent(it))
            }
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun updateToMain() {
        this.isMain = true
    }

    fun updateToSub() {
        this.isMain = false
    }

    fun updateIndex(index: Int) {
        check(index >= 0) { "Index must be greater than or equal to 0" }
        this.index = index
    }

    fun delete() {
        this.registerEvent(SpaceEvent.SpaceDeletedEvent(this))
    }
}
