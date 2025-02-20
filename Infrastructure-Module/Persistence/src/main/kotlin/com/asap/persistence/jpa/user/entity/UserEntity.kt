package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserEntity(
    id: String,
    username: String,
    email: String,
    profileImage: String,
    userPermission: UserPermissionEntity,
    birthday: LocalDate?,
    onboardingAt: LocalDateTime?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : AggregateRoot<UserEntity>(id, createdAt, updatedAt) {
    @Column(nullable = false)
    val username: String = username

    @Column(nullable = false)
    val profileImage: String = profileImage

    @Column(
        columnDefinition = "varchar(100)",
    )
    val email: String = email

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userPermission: UserPermissionEntity = userPermission

    val birthday: LocalDate? = birthday

    val onboardingAt: LocalDateTime? = onboardingAt
}
