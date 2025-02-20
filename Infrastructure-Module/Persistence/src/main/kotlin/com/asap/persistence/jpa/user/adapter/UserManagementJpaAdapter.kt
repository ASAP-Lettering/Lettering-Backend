package com.asap.persistence.jpa.user.adapter

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.UserManagementPort
import com.asap.common.event.EventPublisher
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.persistence.jpa.user.UserMapper
import com.asap.persistence.jpa.user.repository.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserManagementJpaAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val eventPublisher: EventPublisher,
) : UserManagementPort {
    override fun save(user: User): User {
        val userEntity = UserMapper.toUserEntity(user)
        userJpaRepository.save(userEntity)
        eventPublisher.publishAll(user.pullEvents())
        return user
    }

    override fun getUser(userId: DomainId): User? = userJpaRepository.findByIdOrNull(userId.value)?.let { UserMapper.toUser(it) }

    override fun getUserNotNull(userId: DomainId): User =
        userJpaRepository.findByIdOrNull(userId.value)?.let { UserMapper.toUser(it) }
            ?: throw UserException.UserNotFoundException()

    override fun findById(userId: DomainId): User? {
        return userJpaRepository.findByIdOrNull(userId.value)?.let { UserMapper.toUser(it) }
    }
}
