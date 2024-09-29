package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.*

@Entity
@Table(
    name = "user_auth",
    indexes = [
        Index(name = "idx_social_id_index", columnList = "socialId"),
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    lateinit var user: UserEntity

    @Column(nullable = false)
    val socialId: String = socialId

    @Column(nullable = false)
    val socialLoginProvider: String = socialLoginProvider
}
