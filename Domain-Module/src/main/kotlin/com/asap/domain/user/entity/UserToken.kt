package com.asap.domain.user.entity

import com.asap.domain.common.BaseEntity
import com.asap.domain.common.DomainId
import java.time.LocalDateTime

class UserToken(
    id: DomainId,
    val userId: DomainId? = null,
    val token: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
): BaseEntity(id, createdAt, updatedAt) {
    companion object {
        fun create(
            userId: DomainId? = null,
            token: String,
        ): UserToken =
            UserToken(
                id = DomainId.generate(),
                userId = userId,
                token = token,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
    }
}
