package com.asap.domain.user.entity

import com.asap.domain.common.DomainId
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate

data class User(
    val id: DomainId = DomainId.generate(),
    val username: String,
    val profileImage: String,
    val permission: UserPermission,
    val birthday: LocalDate?,
)
