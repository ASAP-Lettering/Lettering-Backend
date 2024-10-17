package com.asap.domain.user.entity

import com.asap.domain.common.DomainId
import com.asap.domain.user.enums.SocialLoginProvider

data class UserAuth(
    val id: DomainId = DomainId.generate(),
    val userId: DomainId,
    var socialId: String,
    val socialLoginProvider: SocialLoginProvider,
) {
    fun delete()  {
        this.socialId = "UNKNOWN"
    }
}
