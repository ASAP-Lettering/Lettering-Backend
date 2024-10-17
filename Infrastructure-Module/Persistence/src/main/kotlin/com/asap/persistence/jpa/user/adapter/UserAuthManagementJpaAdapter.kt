package com.asap.persistence.jpa.user.adapter

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.persistence.jpa.user.UserMapper
import com.asap.persistence.jpa.user.repository.UserAuthJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserAuthManagementJpaAdapter(
    private val userAuthJpaRepository: UserAuthJpaRepository,
) : UserAuthManagementPort {
    override fun getUserAuth(
        socialId: String,
        socialLoginProvider: SocialLoginProvider,
    ): UserAuth? =
        userAuthJpaRepository
            .findBySocialIdAndSocialLoginProvider(socialId, socialLoginProvider.name)
            ?.let { UserMapper.toUserAuth(it) }

    override fun getNotNull(userId: DomainId): UserAuth {
        val userAuthEntity =
            userAuthJpaRepository.findByUserId(userId.value)
                ?: throw UserException.UserAuthNotFoundException()
        return UserMapper.toUserAuth(userAuthEntity)
    }

    override fun isExistsUserAuth(
        socialId: String,
        socialLoginProvider: SocialLoginProvider,
    ): Boolean = userAuthJpaRepository.existsBySocialIdAndSocialLoginProvider(socialId, socialLoginProvider.name)

    override fun saveUserAuth(userAuth: UserAuth): UserAuth {
        val userAuthEntity = UserMapper.toUserAuthEntity(userAuth)
        userAuthJpaRepository.save(userAuthEntity)
        return userAuth
    }
}
