package com.asap.event

import com.asap.common.event.EventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : EventPublisher {
    override fun <T : Any> publishAll(events: List<T>) {
        events.forEach { applicationEventPublisher.publishEvent(it) }
    }
}
