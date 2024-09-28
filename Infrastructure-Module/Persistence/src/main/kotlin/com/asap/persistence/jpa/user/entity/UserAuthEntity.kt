package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "idx_social_id_index", columnList = "socialId"),
        Index(name = "fk_user_id_index", columnList = "userId"),
    ],
)
class UserAuthEntity(
    id: String,
    userId: String,
    socialId: String,
    socialLoginProvider: String,
) : AggregateRoot<UserAuthEntity>(id) {
    @Column(nullable = false)
    val userId: String = userId

    @Column(nullable = false)
    val socialId: String = socialId

    @Column(nullable = false)
    val socialLoginProvider: String = socialLoginProvider
}
