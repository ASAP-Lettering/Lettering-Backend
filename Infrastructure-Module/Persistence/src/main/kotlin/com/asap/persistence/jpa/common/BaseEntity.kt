package com.asap.persistence.jpa.common

import com.asap.domain.common.DomainId
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity(
    @Id
    open val id: String = DomainId.generate().value,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    fun update() {
        this.updatedAt = LocalDateTime.now()
    }
}
