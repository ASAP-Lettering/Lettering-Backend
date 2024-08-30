package com.asap.security.jwt

import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.security.jwt.user.UserJwtProperties
import com.asap.security.jwt.user.UserRegisterJwtClaims
import java.util.*


class TestJwtDataGenerator(
    private val userJwtProperties: UserJwtProperties
) {


    fun generateRegisterToken(
        socialId: String = "socialId",
        socialLoginProvider: String = SocialLoginProvider.KAKAO.name,
        username: String = "username",
        issuedAt: Date = Date()
    ): String{
        return JwtProvider.createToken(
            JwtPayload(
                issuedAt = issuedAt,
                issuer = UserJwtProperties.ISSUER,
                subject= UserJwtProperties.SUBJECT,
                expireTime = UserJwtProperties.REGISTER_TOKEN_EXPIRE_TIME,
                claims = UserRegisterJwtClaims(
                    socialId = socialId,
                    socialLoginProvider = SocialLoginProvider.parse(socialLoginProvider),
                    username = username,
                    profileImage = "profileImage"
                )
            ),
            userJwtProperties.secret
        )
    }

    fun generateInvalidToken(): String = "invalidToken"

}