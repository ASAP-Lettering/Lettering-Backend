package com.asap.domain.user.entity

import com.asap.domain.common.DomainId

/**
 * 토큰 타입이 필요한지 다시 생각해보기
 */
data class UserToken(
    val id: DomainId = DomainId.generate(),
    val token: String,
) {
}