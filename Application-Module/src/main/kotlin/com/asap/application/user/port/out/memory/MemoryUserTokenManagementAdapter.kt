package com.asap.application.user.port.out.memory

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.application.user.vo.UserClaims
import com.asap.common.exception.DefaultException
import com.asap.domain.user.entity.User
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemoryUserTokenManagementAdapter(

) : UserTokenManagementPort{
    override fun resolveRegisterToken(token: String): UserClaims.Register {
        return when(token){
            "valid" -> UserClaims.Register(
                socialId = "123",
                socialLoginProvider = SocialLoginProvider.KAKAO,
                username = "test"
            )
            "duplicate" -> UserClaims.Register(
                socialId = "duplicate",
                socialLoginProvider = SocialLoginProvider.KAKAO,
                username = "test"
            )
            else -> throw DefaultException.InvalidArgumentException() // TODO: jwt 구현할 때 수정
        }
    }

    override fun generateRegisterToken(socialId: String, socialLoginProvider: String, username: String): String {
        return "registerToken"
    }

    override fun generateAccessToken(user: User): String {
        return "accessToken"
    }

    override fun generateRefreshToken(user: User): String {
        return "refreshToken"
    }
}