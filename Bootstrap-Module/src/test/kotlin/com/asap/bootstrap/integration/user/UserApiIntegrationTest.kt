package com.asap.bootstrap.integration.user

import com.asap.application.user.exception.UserException
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.user.dto.LogoutRequest
import com.asap.bootstrap.user.dto.RegisterUserRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import java.time.LocalDate

class UserApiIntegrationTest : IntegrationSupporter() {
    @Test
    fun registerUserSuccessTest() {
        // given
        val userId = userMockManager.settingUser()
        val registerToken = jwtMockManager.generateRegisterToken(userId)
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
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

    @Test
    fun registerUserInvalidTest_with_DuplicateUser() {
        // given
        val userId = userMockManager.settingUser()
        val duplicateRegisterToken = jwtMockManager.generateRegisterToken(userId)
        val request = RegisterUserRequest(duplicateRegisterToken, true, true, true, LocalDate.now(), "realName")
        mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun registerUserInvalidTest_with_InvalidRegisterToken() {
        // given
        val userId = userMockManager.settingUser()
        val registerToken = jwtMockManager.generateInvalidToken()
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") {
                exists()
                isString()
            }
        }
    }

    @Test
    fun registerUserInvalidTest_with_NonSavedRegisterToken() {
        // given
        val registerToken = jwtMockManager.generateInvalidToken()
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") {
                exists()
                isString()
                value(UserException.UserPermissionDeniedException().code)
            }
        }
    }

    @Test
    fun registerUserInvalidTest_with_InvalidServicePermission() {
        // given
        val registerToken = jwtMockManager.generateRegisterToken()
        val request = RegisterUserRequest(registerToken, false, true, true, LocalDate.now(), "realName")
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun registerUserInvalidTest_with_InvalidPrivatePermission() {
        // given
        val registerToken = jwtMockManager.generateRegisterToken()
        val request = RegisterUserRequest(registerToken, true, false, true, LocalDate.now(), "realName")
        // when
        val response =
            mockMvc.post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
        // then
        response.andExpect {
            status { isBadRequest() }
        }
    }

    @Nested
    inner class LogoutTest {
        @Test
        fun logout() {
            // given
            val userId = userMockManager.settingUser()
            val refreshToken = jwtMockManager.generateRefreshToken(userId)
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val request = LogoutRequest(refreshToken)
            // when
            val response =
                mockMvc.delete("/api/v1/users/logout") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun logout_with_InvalidToken() {
            // given
            val userId = userMockManager.settingUser()
            val refreshToken = jwtMockManager.generateInvalidToken()
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val request = LogoutRequest(refreshToken)
            // when
            val response =
                mockMvc.delete("/api/v1/users/logout") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }
            // then
            response.andExpect {
                status { isUnauthorized() }
            }
        }
    }
}
