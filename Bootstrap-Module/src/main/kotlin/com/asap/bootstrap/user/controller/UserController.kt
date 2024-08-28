package com.asap.bootstrap.user.controller

import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.bootstrap.user.api.UserApi
import com.asap.bootstrap.user.dto.RegisterUserRequest
import com.asap.bootstrap.user.dto.RegisterUserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val registerUserUsecase: RegisterUserUsecase,
) : UserApi{

    override fun registerUser(request: RegisterUserRequest): ResponseEntity<RegisterUserResponse> {
        val response = registerUserUsecase.registerUser(
            RegisterUserUsecase.Command(
            request.registerToken,
            request.servicePermission,
            request.privatePermission,
            request.marketingPermission,
            request.birthday
        ))
        return ResponseEntity.ok(RegisterUserResponse(response.accessToken, response.refreshToken))
    }
}