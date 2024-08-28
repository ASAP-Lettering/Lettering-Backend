package com.asap.bootstrap.auth.api

import com.asap.bootstrap.auth.dto.SocialLoginRequest
import com.asap.bootstrap.auth.dto.SocialLoginResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/api/v1/auth")
interface AuthApi {

    @Operation(summary = "소셜 로그인")
    @PostMapping("/login/{provider}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "기존 회원 로그인 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SocialLoginResponse.Success::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "신규 회원 가입 필요",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SocialLoginResponse.NonRegistered::class)
                    )
                ]
            )
        ]
    )
    fun socialLogin(
        @PathVariable provider: String,
        @RequestBody request: SocialLoginRequest
    ): ResponseEntity<SocialLoginResponse>
}