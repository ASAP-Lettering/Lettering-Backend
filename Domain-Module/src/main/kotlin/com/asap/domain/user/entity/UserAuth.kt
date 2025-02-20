package com.asap.domain.user.entity

import com.asap.domain.common.BaseEntity
import com.asap.domain.common.DomainId
import com.asap.domain.user.enums.SocialLoginProvider
import java.time.LocalDateTime

class UserAuth(
    id: DomainId,
    val userId: DomainId,
    var socialId: String,
    val socialLoginProvider: SocialLoginProvider,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : BaseEntity(id, createdAt, updatedAt) {
    companion object {
        fun create(
            userId: DomainId,
            socialId: String,
            socialLoginProvider: SocialLoginProvider,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ): UserAuth =
            UserAuth(
                id = DomainId.generate(),
                userId = userId,
                socialId = socialId,
                socialLoginProvider = socialLoginProvider,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )

    }

    fun delete() {
        this.socialId = "UNKNOWN"
        updateTime()
    }
}
