package com.asap.bootstrap.acceptance.auth.controller

import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.auth.controller.AuthController
import com.asap.bootstrap.auth.dto.SocialLoginRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

@WebMvcTest(AuthController::class)
class AuthControllerTest: AcceptanceSupporter() {


    @MockBean
    private lateinit var socialLoginUsecase: SocialLoginUsecase


    @Test
    fun socialLoginSuccessTest() {
        // given
        val request = SocialLoginRequest("registered")
        val command = SocialLoginUsecase.Command("KAKAO","registered")
        BDDMockito.given(socialLoginUsecase.login(command))
            .willReturn(SocialLoginUsecase.Success("accessToken", "refreshToken"))
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", "KAKAO") {
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

    @Test
    fun socialLoginNonRegisteredTest() {
        // given
        val request = SocialLoginRequest("nonRegistered")
        val command = SocialLoginUsecase.Command("KAKAO","nonRegistered")
        BDDMockito.given(socialLoginUsecase.login(command))
            .willReturn(SocialLoginUsecase.NonRegistered("registerToken"))
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", "KAKAO") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        // then
        response.andExpect {
            status { isUnauthorized() }
            jsonPath("$.registerToken") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }


}