package com.asap.persistence.jpa.user.adapter

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserToken
import com.asap.persistence.jpa.user.UserMapper
import com.asap.persistence.jpa.user.repository.UserTokenJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserTokenManagementJpaAdapter(
    private val userTokenJpaRepository: UserTokenJpaRepository,
) : UserTokenManagementPort {
    override fun isExistsToken(token: String): Boolean = userTokenJpaRepository.existsByToken(token)

    override fun isExistsToken(
        token: String,
        userId: DomainId,
    ): Boolean = userTokenJpaRepository.existsByTokenAndUserId(token, userId.value)

    override fun saveUserToken(userToken: UserToken): UserToken {
        val userTokenEntity = UserMapper.toUserTokenEntity(userToken)
        userTokenJpaRepository.save(userTokenEntity)
        return userToken
    }

    override fun deleteUserToken(token: String) {
        userTokenJpaRepository.deleteByToken(token)
    }
}
