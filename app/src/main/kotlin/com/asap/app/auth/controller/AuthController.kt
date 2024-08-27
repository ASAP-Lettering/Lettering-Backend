package com.asap.app.auth.controller

import com.asap.app.auth.api.AuthApi
import com.asap.app.auth.dto.SocialLoginRequest
import com.asap.app.auth.dto.SocialLoginResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
) : AuthApi {

    override fun socialLogin(
        provider: String,
        request: SocialLoginRequest
    ): ResponseEntity<SocialLoginResponse> {
        when (request.accessToken) {
            "nonRegistered" -> return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(SocialLoginResponse.NonRegistered("registerToken"))

            "registered" -> return ResponseEntity
                .ok(SocialLoginResponse.Success("accessToken", "refreshToken"))

            else -> return ResponseEntity.badRequest().build()
        }
    }
}