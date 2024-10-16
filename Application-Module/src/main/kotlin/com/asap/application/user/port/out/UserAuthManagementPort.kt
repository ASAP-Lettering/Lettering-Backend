package com.asap.application.user.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider

interface UserAuthManagementPort {
    fun getUserAuth(
        socialId: String,
        socialLoginProvider: SocialLoginProvider,
    ): UserAuth?

    fun getNotNull(userId: DomainId): UserAuth

    fun isExistsUserAuth(
        socialId: String,
        socialLoginProvider: SocialLoginProvider,
    ): Boolean

    fun saveUserAuth(userAuth: UserAuth): UserAuth
}
