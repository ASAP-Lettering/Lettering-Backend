package com.asap.persistence.jpa.user

import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.vo.UserPermission
import com.asap.persistence.jpa.user.entity.UserAuthEntity
import com.asap.persistence.jpa.user.entity.UserEntity
import com.asap.persistence.jpa.user.entity.UserPermissionEntity
import com.asap.persistence.jpa.user.entity.UserTokenEntity

object UserMapper {
    fun toUserEntity(user: User): UserEntity =
        UserEntity(
            id = user.id.value,
            username = user.username,
            profileImage = user.profileImage,
            birthday = user.birthday,
            userPermission =
                UserPermissionEntity(
                    servicePermission = user.permission.servicePermission,
                    privatePermission = user.permission.privatePermission,
                    marketingPermission = user.permission.marketingPermission,
                ),
            email = user.email,
            onboardingAt = user.onboardingAt,
        )

    fun toUser(userEntity: UserEntity): User =
        User(
            id = DomainId(userEntity.id),
            username = userEntity.username,
            profileImage = userEntity.profileImage,
            permission =
                UserPermission(
                    servicePermission = userEntity.userPermission.servicePermission,
                    privatePermission = userEntity.userPermission.privatePermission,
                    marketingPermission = userEntity.userPermission.marketingPermission,
                ),
            birthday = userEntity.birthday,
            email = userEntity.email,
            onboardingAt = userEntity.onboardingAt,
        )

    fun toUserAuthEntity(userAuth: UserAuth): UserAuthEntity =
        UserAuthEntity(
            id = userAuth.id.value,
            socialId = userAuth.socialId,
            socialLoginProvider = userAuth.socialLoginProvider.name,
            userId = userAuth.userId.value,
        )

    fun toUserAuth(userAuthEntity: UserAuthEntity): UserAuth =
        UserAuth(
            id = DomainId(userAuthEntity.id),
            socialId = userAuthEntity.socialId,
            socialLoginProvider = SocialLoginProvider.parse(userAuthEntity.socialLoginProvider),
            userId = DomainId(userAuthEntity.userId),
        )

    fun toUserTokenEntity(userToken: UserToken): UserTokenEntity =
        UserTokenEntity(
            id = userToken.id.value,
            token = userToken.token,
            userId = userToken.userId?.value,
        )

    fun toUserToken(userTokenEntity: UserTokenEntity): UserToken =
        UserToken(
            id = DomainId(userTokenEntity.id),
            token = userTokenEntity.token,
            userId = userTokenEntity.userId?.let { DomainId(it) },
        )
}
