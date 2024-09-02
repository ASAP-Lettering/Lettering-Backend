package com.asap.application.user.port.out.memory

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.user.entity.UserToken
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemoryUserTokenManagementAdapter: UserTokenManagementPort {

    private val tokens = mutableSetOf<String>()
    private val userTokens = mutableSetOf<UserToken>()

    override fun isExistsToken(token: String): Boolean {
        return tokens.contains(token)
    }

    override fun saveUserToken(userToken: UserToken): UserToken {
        userTokens.add(userToken)
        tokens.add(userToken.token)
        return userToken
    }
}