package com.asap.persistence.jpa.user.adapter

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.persistence.jpa.user.UserMapper
import com.asap.persistence.jpa.user.repository.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserManagementJpaAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserManagementPort {
    override fun saveUser(user: User): User {
        val userEntity = UserMapper.toUserEntity(user)
        userJpaRepository.save(userEntity)
        return user
    }

    override fun getUser(userId: DomainId): User? = userJpaRepository.findByIdOrNull(userId.value)?.let { UserMapper.toUser(it) }

    override fun getUserNotNull(userId: DomainId): User =
        userJpaRepository.findByIdOrNull(userId.value)?.let { UserMapper.toUser(it) }
            ?: throw UserException.UserNotFoundException()
}
