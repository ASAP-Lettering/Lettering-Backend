package com.asap.domain.space.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.space.event.SpaceEvent
import java.time.LocalDateTime

class Space(
    id: DomainId,
    val userId: DomainId,
    var name: String,
    var index: Int,
    val templateType: Int,
    var isMain: Boolean = false,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : Aggregate<Space>(id, createdAt, updatedAt) {
    companion object {
        fun create(
            id: DomainId,
            userId: DomainId,
            name: String,
            templateType: Int,
            index: Int = -1,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ): Space =
            Space(
                id = id,
                userId = userId,
                name = name,
                templateType = templateType,
                index = index,
                createdAt = createdAt,
                updatedAt = updatedAt,
            ).also {
                it.registerEvent(SpaceEvent.SpaceCreatedEvent(it))
            }
    }

    fun updateName(name: String) {
        this.name = name
        updateTime()
    }

    fun updateToMain() {
        this.isMain = true
        updateTime()
    }

    fun updateToSub() {
        this.isMain = false
        updateTime()
    }

    fun updateIndex(index: Int) {
        check(index >= 0) { "Index must be greater than or equal to 0" }
        this.index = index
        updateTime()
    }

    fun delete() {
        this.registerEvent(SpaceEvent.SpaceDeletedEvent(this))
        updateTime()
    }
}
