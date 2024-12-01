package com.asap.bootstrap.auth.controller

import com.asap.application.user.port.`in`.ReissueTokenUsecase
import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.bootstrap.auth.api.AuthApi
import com.asap.bootstrap.auth.dto.ReissueRequest
import com.asap.bootstrap.auth.dto.ReissueResponse
import com.asap.bootstrap.auth.dto.SocialLoginRequest
import com.asap.bootstrap.auth.dto.SocialLoginResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val socialLoginUsecase: SocialLoginUsecase,
    private val reissueTokenUsecase: ReissueTokenUsecase
) : AuthApi {

    override fun socialLogin(
        provider: String,
        request: SocialLoginRequest
    ): ResponseEntity<SocialLoginResponse> {
        val command = SocialLoginUsecase.Command(
            provider = provider,
            accessToken = request.accessToken
        )
        return when (val response = socialLoginUsecase.login(command)) {
            is SocialLoginUsecase.Success -> ResponseEntity.ok(
                SocialLoginResponse.Success(
                    response.accessToken,
                    response.refreshToken,
                    response.isProcessedOnboarding
                )
            )

            is SocialLoginUsecase.NonRegistered -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(SocialLoginResponse.NonRegistered(response.registerToken))
        }
    }

    override fun reissueToken(request: ReissueRequest): ReissueResponse {
        val response = reissueTokenUsecase.reissue(
            ReissueTokenUsecase.Command(
                refreshToken = request.refreshToken
            )
        )
        return ReissueResponse(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }
}