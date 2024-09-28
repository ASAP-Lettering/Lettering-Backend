package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

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
) : AggregateRoot<UserTokenEntity>(id) {
    @Column(nullable = false, unique = true, length = 500)
    val token: String = token
}
