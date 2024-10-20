package com.asap.common.event

interface EventPublisher {
    fun <T : Any> publishAll(events: List<T>)
}
