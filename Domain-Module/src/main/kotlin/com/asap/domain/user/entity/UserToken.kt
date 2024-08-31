package com.asap.domain.user.entity

import com.asap.domain.common.DomainId
import com.asap.domain.user.enums.TokenType

data class UserToken(
    val id: DomainId = DomainId.generate(),
    val token: String,
    val type: TokenType
) {
}