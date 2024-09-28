package com.asap.persistence.jpa.user.repository

import com.asap.persistence.jpa.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, String>
