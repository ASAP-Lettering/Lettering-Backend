package com.asap.domain.common

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime

abstract class Aggregate<T : Aggregate<T>>(
    id: DomainId,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
): BaseEntity(id, createdAt, updatedAt) {
    private val logger = KotlinLogging.logger {}
    private val events: MutableList<DomainEvent<T>> = mutableListOf()

    fun registerEvent(event: DomainEvent<T>) {
        events.add(event)
    }

    fun pullEvents(): List<DomainEvent<T>> {
        val pulledEvents = events.toList()
        logger.info { "Pulled events: $pulledEvents" }
        events.clear()
        return pulledEvents
    }
}
