package com.asap.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey


object JwtProvider {

    inline fun <reified T: JwtClaims> createToken(
        jwtPayload: JwtPayload<T>,
        secretKey: String
    ): String{
        return Jwts.builder()
            .issuer(jwtPayload.issuer)
            .subject(jwtPayload.subject)
            .claims(jwtPayload.claims.getClaims())
            .issuedAt(jwtPayload.issuedAt)
            .signWith(generateKey(secretKey))
            .expiration(Date(jwtPayload.issuedAt.time + jwtPayload.expireTime))
            .compact()

    }

    fun generateKey(
        secretKey: String
    ): SecretKey{
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    inline fun <reified T: JwtClaims> resolveToken(
        token: String,
        secret: String,
    ): JwtPayload<T>{
        val claims = Jwts.parser()
            .verifyWith(generateKey(secret))
            .build()
            .parseSignedClaims(token)
            .payload

        return JwtPayload(
            issuedAt = claims.issuedAt,
            issuer = claims.issuer,
            subject = claims.subject,
            expireTime = claims.expiration.time - claims.issuedAt.time,
            claims = JwtClaims.convertFromClaims(claims)
        )
    }



}