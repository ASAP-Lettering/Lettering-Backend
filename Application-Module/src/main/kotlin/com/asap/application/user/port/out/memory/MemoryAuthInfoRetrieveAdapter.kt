package com.asap.application.user.port.out.memory

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.AuthInfoRetrievePort
import com.asap.application.user.vo.AuthInfo
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemoryAuthInfoRetrieveAdapter: AuthInfoRetrievePort {

    private val authInfos = mutableMapOf<Pair<String, SocialLoginProvider>, AuthInfo>().apply {
        put(Pair("registered", SocialLoginProvider.KAKAO), AuthInfo(SocialLoginProvider.KAKAO, "socialId", "username"))
        put(Pair("nonRegistered", SocialLoginProvider.KAKAO), AuthInfo(SocialLoginProvider.KAKAO, "nonRegisteredId", "username"))
    }

    override fun getAuthInfo(provider: SocialLoginProvider, accessToken: String): AuthInfo {
        return authInfos[Pair(accessToken, provider)] ?: throw UserException.UserAuthNotFoundException()
    }
}