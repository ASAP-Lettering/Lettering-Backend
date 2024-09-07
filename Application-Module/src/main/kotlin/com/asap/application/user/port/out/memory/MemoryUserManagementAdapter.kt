package com.asap.application.user.port.out.memory

import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.vo.UserPermission
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemoryUserManagementAdapter: UserManagementPort {
    private val users = mutableMapOf<DomainId, User>().apply {
        put(DomainId("registered"), User(DomainId("registered"), "username", "profileImage",UserPermission(true,true,true), null))
    }

    override fun saveUser(user: User): User {
        users[user.id] = user
        return user
    }

    override fun getUser(userId: DomainId): User? {
        return users[userId]
    }
}