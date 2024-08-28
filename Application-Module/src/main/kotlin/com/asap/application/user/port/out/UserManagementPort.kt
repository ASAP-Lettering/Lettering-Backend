package com.asap.application.user.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User

interface UserManagementPort {
    fun saveUser(user: User): User

    fun getUser(userId: DomainId): User?
}