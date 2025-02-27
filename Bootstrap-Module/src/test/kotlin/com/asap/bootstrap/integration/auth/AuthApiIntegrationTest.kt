package com.asap.bootstrap.integration.auth

import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.auth.dto.ReissueRequest
import com.asap.bootstrap.web.auth.dto.SocialLoginRequest
import com.asap.client.KakaoTestData
import com.asap.client.MockServer
import com.asap.security.jwt.user.TokenType
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.Test

class AuthApiIntegrationTest : IntegrationSupporter() {
    @Autowired
    lateinit var mockWebServer: MockServer

    @Test
    fun socialLoginSuccessTest() {
        // given
        val request = SocialLoginRequest("registered")
        val provider = "KAKAO"
        mockWebServer.enqueue(KakaoTestData.KAKAO_OAUTH_SUCCESS_RESPONSE)
        val userId = userMockManager.settingUser()
        userMockManager.settingUserAuth(userId, "socialId", provider)
        // when
        val response =
            mockMvc.post("/api/v1/auth/login/{provider}", provider) {
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
        val provider = "KAKAO"
        mockWebServer.enqueue(KakaoTestData.KAKAO_OAUTH_FAIL_RESPONSE_WITH_NON_REGISTERED)
        // when
        val response =
            mockMvc.post("/api/v1/auth/login/{provider}", provider) {
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
    fun socialLoginBadRequestTest_with_invalid_access_token() {
        // given
        val request = SocialLoginRequest("invalid")
        val provider = "KAKAO"
        mockWebServer.enqueue(KakaoTestData.KAKAO_OAUTH_FAIL_RESPONSE_WITH_INVALID_ACCESS_TOKEN)
        // when
        val response =
            mockMvc.post("/api/v1/auth/login/{provider}", provider) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isNotFound() }
        }
    }

    @Nested
    inner class ReissueTest {
        @Test
        fun reissueTokenTest() {
            // given
            val userId = userMockManager.settingUser()
            val refreshToken =
                jwtMockManager.generateRefreshToken(
                    userId,
                    issuedAt =
                        Date(
                            LocalDateTime
                                .now()
                                .minusHours(1)
                                .toInstant(
                                    ZoneOffset.UTC,
                                ).toEpochMilli(),
                        ),
                )
            val request = ReissueRequest(refreshToken)
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

        @Test
        fun reissueTokenTest_With_Expired_Token() {
            // given
            val userId = userMockManager.settingUser()
            val refreshToken = jwtMockManager.generateExpiredToken(TokenType.REFRESH, userId)
            val request = ReissueRequest(refreshToken)
            // when
            val response =
                mockMvc.post("/api/v1/auth/reissue") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
            // then
            response.andExpect {
                status { isUnauthorized() }
            }
        }

        @Test
        fun reissueTokenTest_With_Non_Saved_Token() {
            // given
            val request = ReissueRequest("invalidToken")
            // when
            val response =
                mockMvc.post("/api/v1/auth/reissue") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
            // then
            response
                .andExpect {
                    status { isUnauthorized() }
                }.andDo {
                    print()
                }
        }
    }
}
