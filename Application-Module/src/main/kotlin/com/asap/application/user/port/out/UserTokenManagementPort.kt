package com.asap.application.user.port.out

import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.enums.TokenType

interface UserTokenManagementPort {

    fun isExistsToken(token: String, tokenType: TokenType): Boolean

    fun saveUserToken(userToken: UserToken): UserToken


}