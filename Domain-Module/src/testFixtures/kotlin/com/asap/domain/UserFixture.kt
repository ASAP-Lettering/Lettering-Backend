package com.asap.domain

import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate

object UserFixture {
    fun createUser(
        id: DomainId = DomainId.generate(),
        username: String = "nickname",
    ): User {
        // use instancio
        return User.create(
            id = id,
            username = username,
            profileImage = "profileImage",
            permission = UserPermission(true, true, true),
            birthday = LocalDate.now(),
            email = "email",
        )
    }

    fun createUserAuth(
        userId: String = "userId",
        socialId: String = "socialId",
        socialLoginProvider: SocialLoginProvider = SocialLoginProvider.KAKAO,
    ): UserAuth =
        UserAuth.create(
            userId = DomainId(userId),
            socialId = socialId,
            socialLoginProvider = socialLoginProvider,
        )
}
