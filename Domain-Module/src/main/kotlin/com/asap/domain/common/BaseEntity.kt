package com.asap.domain.common

import java.time.LocalDateTime

abstract class BaseEntity(
    val id: DomainId = DomainId.generate(),
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
) {

    protected fun updateTime() {
        updatedAt = LocalDateTime.now()
    }
}