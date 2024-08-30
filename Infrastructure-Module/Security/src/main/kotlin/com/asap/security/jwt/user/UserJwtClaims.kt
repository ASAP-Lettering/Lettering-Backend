package com.asap.security.jwt.user

import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.enums.TokenType
import com.asap.security.jwt.JwtClaims

class UserJwtClaims(
    val tokenType: TokenType,
    val userId: String,
): JwtClaims {
}

class UserRegisterJwtClaims(
    val socialId: String,
    val socialLoginProvider: SocialLoginProvider,
    val username: String,
): JwtClaims {
    val tokenType: TokenType = TokenType.REGISTER
}

