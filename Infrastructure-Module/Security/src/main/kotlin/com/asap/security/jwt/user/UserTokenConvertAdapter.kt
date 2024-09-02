package com.asap.security.jwt.user

import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.vo.UserClaims
import com.asap.domain.user.entity.User
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.enums.TokenType
import com.asap.security.jwt.JwtClaims
import com.asap.security.jwt.JwtPayload
import com.asap.security.jwt.JwtProvider
import com.asap.security.jwt.JwtProvider.resolveToken
import com.asap.security.jwt.exception.TokenException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Component

@Component
class UserTokenConvertAdapter(
    private val userJwtProperties: UserJwtProperties
) : UserTokenConvertPort {
    override fun resolveRegisterToken(token: String): UserClaims.Register {
        val jwtPayload: JwtPayload<UserRegisterJwtClaims> = resolveToken { resolveToken(token, userJwtProperties.secret) }
        val jwtClaims = jwtPayload.claims
        return UserClaims.Register(
            socialId = jwtClaims.socialId,
            socialLoginProvider = jwtClaims.socialLoginProvider,
            username = jwtClaims.username,
            profileImage = jwtClaims.profileImage
        )
    }

    override fun generateRegisterToken(
        socialId: String,
        socialLoginProvider: String,
        username: String,
        profileImage: String
    ): String {
        val jwtClaims = UserRegisterJwtClaims(
            socialId = socialId,
            socialLoginProvider = SocialLoginProvider.parse(socialLoginProvider),
            username = username,
            profileImage = profileImage
        )
        val payload = getDefaultPayload(jwtClaims, UserJwtProperties.REGISTER_TOKEN_EXPIRE_TIME)
        return JwtProvider.createToken(payload, userJwtProperties.secret)
    }

    override fun generateAccessToken(user: User): String {
        val jwtClaims = UserJwtClaims(
            tokenType = TokenType.ACCESS,
            userId = user.id.id
        )
        val payload = getDefaultPayload(jwtClaims, UserJwtProperties.ACCESS_TOKEN_EXPIRE_TIME)
        return JwtProvider.createToken(payload, userJwtProperties.secret)
    }

    override fun generateRefreshToken(user: User): String {
        val jwtClaims = UserJwtClaims(
            tokenType = TokenType.REFRESH,
            userId = user.id.id
        )
        val payload = getDefaultPayload(jwtClaims, UserJwtProperties.REFRESH_TOKEN_EXPIRE_TIME)
        return JwtProvider.createToken(payload, userJwtProperties.secret)
    }

    private fun <T : JwtClaims> getDefaultPayload(
        jwtClaims: T,
        expireTime: Long
    ): JwtPayload<T> {
        return JwtPayload(
            issuer = UserJwtProperties.ISSUER,
            subject = UserJwtProperties.SUBJECT,
            expireTime = expireTime,
            claims = jwtClaims,
        )
    }

    private fun <T : JwtClaims> resolveToken(resolve: () -> JwtPayload<T>): JwtPayload<T> {
        try {
            return resolve()
        } catch (e: Exception){
            when(e){
                is MalformedJwtException -> throw TokenException.InvalidTokenException()
                is ExpiredJwtException -> throw TokenException.ExpiredTokenException()
                else -> throw e
            }
        }
    }
}