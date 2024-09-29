package com.asap.domain.user.entity

import com.asap.domain.common.DomainId

data class UserToken(
    val id: DomainId = DomainId.generate(),
    val token: String,
)
