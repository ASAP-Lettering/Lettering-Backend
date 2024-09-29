package com.asap.persistence.jpa.user.entity

import com.asap.persistence.jpa.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "user_permission")
class UserPermissionEntity(
    val servicePermission: Boolean,
    val privatePermission: Boolean,
    val marketingPermission: Boolean,
) : BaseEntity()
