package com.asap.application.user

import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate

class UserMockManager(
    private val tokenManagementPort: UserTokenManagementPort,
    private val userManagementPort: UserManagementPort,
    private val userAuthManagementPort: UserAuthManagementPort,
) {
    fun settingUser(
        userId: String = DomainId.generate().value,
        username: String = "nickname",
    ): String {
        val user =
            userManagementPort.saveUser(
                User(
                    id = DomainId(userId),
                    username = username,
                    profileImage = "profileImage",
                    permission = UserPermission(true, true, true),
                    birthday = LocalDate.now(),
                    email = "email",
                ),
            )
        return user.id.value
    }

    fun settingUserAuth(
        userId: String,
        socialId: String = "socialId",
        provider: String = "KAKAO",
    ) {
        userAuthManagementPort.saveUserAuth(
            UserAuth(
                userId = DomainId(userId),
                socialId = socialId,
                socialLoginProvider = SocialLoginProvider.parse(provider),
            ),
        )
    }
}
