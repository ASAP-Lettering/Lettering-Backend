package com.asap.application.user.port.out

import com.asap.application.user.exception.UserException
import com.asap.application.user.vo.AuthInfo
import com.asap.domain.user.enums.SocialLoginProvider

interface AuthInfoRetrievePort {
    @Throws(UserException.UserAuthNotFoundException::class)
    fun getAuthInfo(
        provider: SocialLoginProvider,
        accessToken: String,
    ): AuthInfo

    fun getAccessToken(
        provider: SocialLoginProvider,
        code: String,
        state: String,
    ): String
}
