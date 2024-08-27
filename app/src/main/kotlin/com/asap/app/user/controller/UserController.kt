package com.asap.app.user.controller

import com.asap.app.user.api.UserApi
import com.asap.app.user.dto.RegisterUserRequest
import com.asap.app.user.dto.RegisterUserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(

) : UserApi{

    override fun registerUser(request: RegisterUserRequest): ResponseEntity<RegisterUserResponse> {
        when(request.registerToken){
            "register" -> return ResponseEntity.ok(RegisterUserResponse("accessToken", "refreshToken"))
            else -> return ResponseEntity.badRequest().build()
        }
    }
}