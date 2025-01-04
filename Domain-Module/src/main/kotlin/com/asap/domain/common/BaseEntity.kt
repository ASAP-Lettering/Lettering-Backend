package com.asap.domain.common

abstract class BaseEntity(
    val id: DomainId = DomainId.generate()
) {
}