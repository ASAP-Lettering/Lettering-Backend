package com.asap.security.jwt.user

import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.security.jwt.JwtClaims

class UserJwtClaims(
    val userId: String,
    val tokenType: TokenType
): JwtClaims {
}

class UserRegisterJwtClaims(
    val socialId: String,
    val socialLoginProvider: SocialLoginProvider,
    val username: String,
    val profileImage: String,
): JwtClaims {}

