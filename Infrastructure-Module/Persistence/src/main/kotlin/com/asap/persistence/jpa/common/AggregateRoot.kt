package com.asap.persistence.jpa.common

import com.asap.domain.common.DomainId

abstract class AggregateRoot<T : AggregateRoot<T>>(
    override val id: String = DomainId.generate().value,
) : BaseEntity(id)
