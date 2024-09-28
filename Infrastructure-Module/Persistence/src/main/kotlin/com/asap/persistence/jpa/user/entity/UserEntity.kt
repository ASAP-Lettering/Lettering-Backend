package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class UserEntity(
    id: String,
    username: String,
    profileImage: String,
    userPermissionEntity: UserPermissionEntity,
    birthday: LocalDate?,
) : AggregateRoot<UserEntity>(id) {
    @Column(nullable = false)
    val username: String = username

    @Column(nullable = false)
    val profileImage: String = profileImage

    @OneToOne(targetEntity = UserPermissionEntity::class, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(nullable = false, name = "user_permission_id")
    val userPermissionEntity: UserPermissionEntity = userPermissionEntity

    val birthday: LocalDate? = birthday
}
