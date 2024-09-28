package com.asap.persistence.jpa.common

import com.asap.domain.common.DomainId
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity(
    @Id
    open val id: String = DomainId.generate().value,
) {
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
}
