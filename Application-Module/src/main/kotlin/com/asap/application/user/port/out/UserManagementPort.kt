package com.asap.application.user.port.out

import com.asap.application.user.exception.UserException
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User

interface UserManagementPort {
    fun saveUser(user: User): User

    fun getUser(userId: DomainId): User?

    @Throws(UserException.UserNotFoundException::class)
    fun getUserNotNull(userId: DomainId): User
}