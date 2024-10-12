package com.asap.bootstrap.acceptance.auth.controller

import com.asap.application.user.port.`in`.LogoutUsecase
import com.asap.application.user.port.`in`.ReissueTokenUsecase
import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.application.user.port.`in`.TokenResolveUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.bootstrap.auth.dto.ReissueRequest
import com.asap.bootstrap.auth.dto.SocialLoginRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthControllerTest : AcceptanceSupporter() {
    @MockBean
    private lateinit var socialLoginUsecase: SocialLoginUsecase

    @MockBean
    private lateinit var reissueTokenUsecase: ReissueTokenUsecase

    @MockBean
    private lateinit var tokenResolveUsecase: TokenResolveUsecase

    @MockBean
    private lateinit var logoutUsecase: LogoutUsecase

    @Test
    fun socialLoginSuccessTest() {
        // given
        val request = SocialLoginRequest("registered")
        val command = SocialLoginUsecase.Command("KAKAO", "registered")
        BDDMockito
            .given(socialLoginUsecase.login(command))
            .willReturn(SocialLoginUsecase.Success("accessToken", "refreshToken"))
        // when
        val response =
            mockMvc.post("/api/v1/auth/login/{provider}", "KAKAO") {
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
        val command = SocialLoginUsecase.Command("KAKAO", "nonRegistered")
        BDDMockito
            .given(socialLoginUsecase.login(command))
            .willReturn(SocialLoginUsecase.NonRegistered("registerToken"))
        // when
        val response =
            mockMvc.post("/api/v1/auth/login/{provider}", "KAKAO") {
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

    @Test
    fun reissueTokenTest() {
        // given
        val request = ReissueRequest("refreshToken")
        BDDMockito
            .given(reissueTokenUsecase.reissue(ReissueTokenUsecase.Command(request.refreshToken)))
            .willReturn(ReissueTokenUsecase.Response("accessToken", "refreshToken"))
        // when
        val response =
            mockMvc.post("/api/v1/auth/reissue") {
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
