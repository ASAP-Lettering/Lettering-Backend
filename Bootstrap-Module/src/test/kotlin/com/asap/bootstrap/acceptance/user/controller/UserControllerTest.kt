package com.asap.bootstrap.acceptance.user.controller

import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.user.dto.RegisterUserRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.time.LocalDate

class UserControllerTest : AcceptanceSupporter() {
    @MockBean
    private lateinit var registerUserUsecase: RegisterUserUsecase

    @Test
    fun registerUserTest()  {
        // given
        val request = RegisterUserRequest("register", true, true, true, LocalDate.now(), "realName")
        val command =
            RegisterUserUsecase.Command(
                request.registerToken,
                request.servicePermission,
                request.privatePermission,
                request.marketingPermission,
                request.birthday,
                request.realName,
            )
        given(registerUserUsecase.registerUser(command)).willReturn(RegisterUserUsecase.Response("accessToken", "refreshToken"))
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") {
                exists()
                isString()
                isNotEmpty()
            }
            jsonPath("$.refreshToken") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }
}
