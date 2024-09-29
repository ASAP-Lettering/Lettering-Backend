package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user")
class UserEntity(
    id: String,
    username: String,
    profileImage: String,
    userPermission: UserPermissionEntity,
    birthday: LocalDate?,
) : AggregateRoot<UserEntity>(id) {
    @Column(nullable = false)
    val username: String = username

    @Column(nullable = false)
    val profileImage: String = profileImage

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userPermission: UserPermissionEntity = userPermission

    val birthday: LocalDate? = birthday
}
