package com.asap.domain.common

import io.github.oshai.kotlinlogging.KotlinLogging

abstract class Aggregate<T : Aggregate<T>>(
    val id: DomainId,
) {
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
