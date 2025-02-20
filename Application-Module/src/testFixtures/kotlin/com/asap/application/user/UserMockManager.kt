package com.asap.application.user

import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate
import java.time.LocalDateTime

class UserMockManager(
    private val userManagementPort: UserManagementPort,
    private val userAuthManagementPort: UserAuthManagementPort,
) {
    fun settingUser(
        userId: String = DomainId.generate().value,
        username: String = "nickname",
    ): String =
        settingUserWithUserDomain(
            userId = userId,
            username = username,
        ).id.value

    fun settingUserWithUserDomain(
        userId: String = DomainId.generate().value,
        username: String = "nickname",
    ) = userManagementPort.save(
        User(
            id = DomainId(userId),
            username = username,
            profileImage = "profileImage",
            permission = UserPermission(true, true, true),
            birthday = LocalDate.now(),
            email = "email",
            onboardingAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ),
    )

    fun settingUserAuth(
        userId: String,
        socialId: String = "socialId",
        provider: String = "KAKAO",
    ) {
        userAuthManagementPort.saveUserAuth(
            UserAuth.create(
                userId = DomainId(userId),
                socialId = socialId,
                socialLoginProvider = SocialLoginProvider.parse(provider),
            ),
        )
    }
}
