package com.asap.persistence.jpa.user.repository

import com.asap.persistence.jpa.user.entity.UserTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserTokenJpaRepository : JpaRepository<UserTokenEntity, String> {

    fun existsByToken(token: String): Boolean

    fun deleteByToken(token: String)
}
