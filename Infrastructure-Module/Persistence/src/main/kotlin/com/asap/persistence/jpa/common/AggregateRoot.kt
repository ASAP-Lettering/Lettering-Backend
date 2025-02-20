package com.asap.persistence.jpa.common

import java.time.LocalDateTime

abstract class AggregateRoot<T : AggregateRoot<T>>(
    override val id: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : BaseEntity(id, createdAt, updatedAt)
