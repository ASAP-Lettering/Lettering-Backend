package com.asap.application.user.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserToken

interface UserTokenManagementPort {
    fun isExistsToken(token: String): Boolean

    fun isExistsToken(
        token: String,
        userId: DomainId,
    ): Boolean

    fun saveUserToken(userToken: UserToken): UserToken

    fun deleteUserToken(token: String)
}
