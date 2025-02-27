package com.asap.bootstrap.web.user.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.web.user.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "User API")
@RequestMapping("/api/v1/users")
interface UserApi {
    @Operation(summary = "회원 가입")
    @PostMapping()
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원 가입 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RegisterUserResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun registerUser(
        @RequestBody request: RegisterUserRequest,
    ): RegisterUserResponse

    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공",
            ),
            ApiResponse(
                responseCode = "401",
                description = "로그아웃 실패 - 토큰 없음",
            ),
        ],
    )
    fun logout(
        @AccessUser userId: String,
        @RequestBody request: LogoutRequest,
    )

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원 탈퇴 성공",
            ),
            ApiResponse(
                responseCode = "401",
                description = "회원 탈퇴 실패 - 토큰 없음",
            ),
        ],
    )
    fun deleteUser(
        @AccessUser userId: String,
        @RequestBody(required = false) request: UnregisterUserRequest?,
    )

    @Operation(summary = "내 정보 조회")
    @GetMapping("/info/me")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "내 정보 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserInfoResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "내 정보 조회 실패 - 토큰 없음",
            ),
        ],
    )
    fun getRequestUserInfo(
        @AccessUser userId: String,
    ): UserInfoResponse

    @Operation(summary = "내 생일 수정")
    @PutMapping("/info/me/birthday")
    fun updateBirthday(
        @AccessUser userId: String,
        @RequestBody request: UpdateBirthdayRequest,
    )

    @Operation(summary = "온보딩 여부 저장")
    @PutMapping("/info/onboarding")
    fun updateOnboarding(
        @AccessUser userId: String,
    )
}
