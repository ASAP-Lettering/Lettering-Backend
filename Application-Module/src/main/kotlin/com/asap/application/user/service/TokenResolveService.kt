package com.asap.application.user.service

import com.asap.application.user.port.`in`.TokenResolveUsecase
import com.asap.application.user.port.out.UserTokenConvertPort
import org.springframework.stereotype.Service

@Service
class TokenResolveService(
    private val userTokenConvertPort: UserTokenConvertPort
): TokenResolveUsecase {
    override fun resolveAccessToken(token: String): TokenResolveUsecase.Response {
        val userClaims = userTokenConvertPort.resolveAccessToken(token)
        return TokenResolveUsecase.Response(
            userId = userClaims.userId
        )
    }
}