package com.asap.application.user.port.out.memory

import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemoryAuthManagementAdapter:  UserAuthManagementPort{

    private val userAuths = mutableMapOf<Pair<String, SocialLoginProvider>, UserAuth>().apply {
        put(Pair("socialId", SocialLoginProvider.KAKAO), UserAuth(userId = DomainId("registered"), socialId = "socialId", socialLoginProvider = SocialLoginProvider.KAKAO))
    }

    override fun getUserAuth(socialId: String, socialLoginProvider: SocialLoginProvider): UserAuth? {
        return userAuths[Pair(socialId, socialLoginProvider)]
    }

    override fun isExistsUserAuth(socialId: String, socialLoginProvider: SocialLoginProvider): Boolean {
        return socialId == "duplicate"
    }

    override fun saveUserAuth(userAuth: UserAuth): UserAuth {
        userAuths[Pair(userAuth.socialId, userAuth.socialLoginProvider)] = userAuth
        return userAuth
    }
}