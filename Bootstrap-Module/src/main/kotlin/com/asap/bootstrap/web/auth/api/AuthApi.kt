package com.asap.bootstrap.web.auth.api

import com.asap.bootstrap.common.exception.ExceptionResponse
import com.asap.bootstrap.web.auth.dto.ReissueRequest
import com.asap.bootstrap.web.auth.dto.ReissueResponse
import com.asap.bootstrap.web.auth.dto.SocialLoginRequest
import com.asap.bootstrap.web.auth.dto.SocialLoginResponse
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
                        schema = Schema(implementation = SocialLoginResponse.Success::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "신규 회원 가입 필요",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SocialLoginResponse.NonRegistered::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "4XX",
                description = "소셜 로그인 실패",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ExceptionResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun socialLogin(
        @Schema(description = "소셜 로그인 플랫폼, ex) KAKAO, GOOGLE, NAVER")
        @PathVariable provider: String,
        @RequestBody request: SocialLoginRequest,
    ): ResponseEntity<SocialLoginResponse>

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "토큰 재발급 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ReissueResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "4XX",
                description = "토큰 재발급 실패, 다시 로그인해야함",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ExceptionResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun reissueToken(
        @RequestBody request: ReissueRequest,
    ): ReissueResponse
}
