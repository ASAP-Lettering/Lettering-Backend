package com.asap.security.jwt

import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.UserToken
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.security.jwt.user.TokenType
import com.asap.security.jwt.user.UserJwtClaims
import com.asap.security.jwt.user.UserJwtProperties
import com.asap.security.jwt.user.UserRegisterJwtClaims
import java.util.*

class JwtMockManager(
    private val userJwtProperties: UserJwtProperties,
    private val userTokenManagementPort: UserTokenManagementPort,
) {
    fun generateRegisterToken(
        socialId: String = DomainId.generate().value,
        socialLoginProvider: String = SocialLoginProvider.KAKAO.name,
        username: String = "username",
        issuedAt: Date = Date(),
    ): String =
        JwtProvider
            .createToken(
                JwtPayload(
                    issuedAt = issuedAt,
                    issuer = UserJwtProperties.ISSUER,
                    subject = UserJwtProperties.SUBJECT,
                    expireTime = UserJwtProperties.REGISTER_TOKEN_EXPIRE_TIME,
                    claims =
                        UserRegisterJwtClaims(
                            socialId = socialId,
                            socialLoginProvider = SocialLoginProvider.parse(socialLoginProvider),
                            username = username,
                            profileImage = "profileImage",
                            email = "email",
                        ),
                ),
                userJwtProperties.secret,
            ).apply {
                userTokenManagementPort.saveUserToken(UserToken.create(token = this))
            }

    fun generateAccessToken(
        userId: String = DomainId.generate().value,
        issuedAt: Date = Date(),
    ): String =
        JwtProvider.createToken(
            JwtPayload(
                issuedAt = issuedAt,
                issuer = UserJwtProperties.ISSUER,
                subject = UserJwtProperties.SUBJECT,
                expireTime = UserJwtProperties.ACCESS_TOKEN_EXPIRE_TIME,
                claims =
                    UserJwtClaims(
                        userId = userId,
                        tokenType = TokenType.ACCESS,
                    ),
            ),
            userJwtProperties.secret,
        )

    fun generateRefreshToken(
        userId: String = "userId",
        issuedAt: Date = Date(),
    ): String =
        JwtProvider
            .createToken(
                JwtPayload(
                    issuedAt = issuedAt,
                    issuer = UserJwtProperties.ISSUER,
                    subject = UserJwtProperties.SUBJECT,
                    expireTime = UserJwtProperties.REFRESH_TOKEN_EXPIRE_TIME,
                    claims =
                        UserJwtClaims(
                            userId = userId,
                            tokenType = TokenType.REFRESH,
                        ),
                ),
                userJwtProperties.secret,
            ).apply {
                userTokenManagementPort.saveUserToken(UserToken.create(token = this, userId = DomainId(userId)))
            }

    fun generateExpiredToken(
        tokenType: TokenType,
        userId: String = "userId",
    ): String =
        JwtProvider
            .createToken(
                JwtPayload(
                    issuedAt = Date(System.currentTimeMillis() - tokenTypeExpireTime(tokenType)),
                    issuer = UserJwtProperties.ISSUER,
                    subject = UserJwtProperties.SUBJECT,
                    expireTime = 1,
                    claims =
                        UserJwtClaims(
                            userId = userId,
                            tokenType = tokenType,
                        ),
                ),
                userJwtProperties.secret,
            ).also {
                when (tokenType) {
                    TokenType.REFRESH ->
                        userTokenManagementPort.saveUserToken(
                            UserToken.create(
                                token = it,
                                userId = DomainId(userId),
                            ),
                        )

                    else -> {}
                }
            }

    private fun tokenTypeExpireTime(tokenType: TokenType): Long =
        when (tokenType) {
            TokenType.ACCESS -> UserJwtProperties.ACCESS_TOKEN_EXPIRE_TIME
            TokenType.REFRESH -> UserJwtProperties.REFRESH_TOKEN_EXPIRE_TIME
        }

    fun generateInvalidToken(): String = "invalidToken"
}
