package com.asap.application.user.vo

import com.asap.domain.user.enums.SocialLoginProvider

data class AuthInfo(
    val socialLoginProvider: SocialLoginProvider,
    val socialId: String,
    val username: String
) {
}