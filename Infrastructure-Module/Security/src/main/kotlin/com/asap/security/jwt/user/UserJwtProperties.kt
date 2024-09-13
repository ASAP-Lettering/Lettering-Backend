package com.asap.security.jwt.user

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt.user")
class UserJwtProperties(
    var secret: String
) {

    companion object{
        const val ISSUER = "asap"
        const val SUBJECT = "asap-auth"
        const val ACCESS_TOKEN_EXPIRE_TIME: Long = 1000 * 60 * 30 // 30분
        const val REFRESH_TOKEN_EXPIRE_TIME: Long = 1000 * 60 * 60 * 24 // 1일
        const val REGISTER_TOKEN_EXPIRE_TIME: Long = 1000 * 60 * 10 // 10분
    }
}