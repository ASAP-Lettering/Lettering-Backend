package com.asap.app.user.api

import com.asap.app.user.dto.RegisterUserRequest
import com.asap.app.user.dto.RegisterUserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

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
                        schema = Schema(implementation = RegisterUserResponse::class)
                    )
                ]
            )
        ]
    )
    fun registerUser(
        @RequestBody request: RegisterUserRequest
    ): ResponseEntity<RegisterUserResponse>
}