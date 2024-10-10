package com.asap.application.user.port.out

import com.asap.application.user.vo.UserClaims
import com.asap.domain.user.entity.User

interface UserTokenConvertPort {
    fun resolveRegisterToken(token: String): UserClaims.Register

    fun generateRegisterToken(
        socialId: String,
        socialLoginProvider: String,
        username: String,
        email: String,
        profileImage: String,
    ): String

    fun generateAccessToken(user: User): String

    fun resolveAccessToken(token: String): UserClaims.Access

    fun generateRefreshToken(user: User): String

    fun resolveRefreshToken(token: String): UserClaims.Refresh
}
