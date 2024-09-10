package com.asap.application.user.vo

import com.asap.domain.user.enums.SocialLoginProvider

class UserClaims {

    data class Register(
        val socialId: String,
        val socialLoginProvider: SocialLoginProvider,
        val username: String,
        val profileImage: String
    )

    data class Access(
        val userId: String
    )

    data class Refresh(
        val userId: String
    )

}