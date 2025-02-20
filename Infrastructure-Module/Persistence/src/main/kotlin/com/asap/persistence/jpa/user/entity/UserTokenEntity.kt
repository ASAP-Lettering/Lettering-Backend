package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_token",
    indexes = [
        Index(name = "uk_token_index", columnList = "token", unique = true),
    ],
)
class UserTokenEntity(
    id: String,
    token: String,
    userId: String?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : AggregateRoot<UserTokenEntity>(id, createdAt, updatedAt) {
    @Column(
        nullable = false,
        columnDefinition = "text",
    )
    val token: String = token

    @Column(length = 500, name = "user_id")
    val userId: String? = userId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: UserEntity? = null
}
