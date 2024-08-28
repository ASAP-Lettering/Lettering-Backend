package com.asap.application.user.port.out

import com.asap.application.user.vo.UserClaims
import com.asap.domain.user.entity.User

interface UserTokenManagementPort {
    fun resolveRegisterToken(token: String): UserClaims.Register

    fun generateRegisterToken(
        socialId: String,
        socialLoginProvider: String,
        username: String
    ): String

    fun generateAccessToken(user: User): String

    fun generateRefreshToken(user: User): String
}