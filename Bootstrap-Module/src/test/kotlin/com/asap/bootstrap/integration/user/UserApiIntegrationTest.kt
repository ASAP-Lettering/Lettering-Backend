package com.asap.bootstrap.integration.user

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.UserManagementPort
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.user.dto.LogoutRequest
import com.asap.bootstrap.web.user.dto.RegisterUserRequest
import com.asap.bootstrap.web.user.dto.UnregisterUserRequest
import com.asap.bootstrap.web.user.dto.UpdateBirthdayRequest
import com.asap.domain.common.DomainId
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

class UserApiIntegrationTest(
    private val userManagementPort: UserManagementPort
) : IntegrationSupporter() {
    @Test
    fun registerUserSuccessTest() {
        // given
        val registerToken = jwtMockManager.generateRegisterToken()
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
        val duplicateRegisterToken = jwtMockManager.generateRegisterToken()
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

    @Nested
    @DisplayName("deleteUser")
    inner class DeleteUser{
        @Test
        fun deleteUser() {
            // given
            val userId = userMockManager.settingUser()
            userMockManager.settingUserAuth(userId = userId)
            val accessToken = jwtMockManager.generateAccessToken(userId)

            // when
            val response =
                mockMvc.delete("/api/v1/users") {
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun deleteUser_with_reason(){
            // given
            val userId = userMockManager.settingUser()
            userMockManager.settingUserAuth(userId = userId)
            val accessToken = jwtMockManager.generateAccessToken(userId)
            val request = UnregisterUserRequest("reason")

            // when
            val response =
                mockMvc.delete("/api/v1/users") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    header("Authorization", "Bearer $accessToken")
                }

            // then
            response.andExpect {
                status { isOk() }
            }
            val user = userManagementPort.findById(DomainId(userId))
            user!!.unregisterReason shouldBe request.reason
        }
    }

    @Test
    fun getRequestUserInfo() {
        // given
        val userId = userMockManager.settingUser()
        userMockManager.settingUserAuth(userId = userId)
        val accessToken = jwtMockManager.generateAccessToken(userId)

        // when
        val response =
            mockMvc.get("/api/v1/users/info/me") {
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", "Bearer $accessToken")
            }

        // then
        response.andExpect {
            status { isOk() }
            jsonPath("$.name") {
                exists()
                isString()
            }
            jsonPath("$.socialPlatform") {
                exists()
                isString()
            }
            jsonPath("$.email") {
                exists()
                isString()
            }
            jsonPath("$.birthday") {
                exists()
                isString()
            }
        }
    }

    @Test
    fun updateBirthday() {
        // given
        val userId = userMockManager.settingUser()
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val request = UpdateBirthdayRequest(LocalDate.now())

        // when
        val response =
            mockMvc.put("/api/v1/users/info/me/birthday") {
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
    fun deleteUser_user_updated_at_updated() {
        // given
        val userId = userMockManager.settingUser()
        userMockManager.settingUserAuth(userId = userId)
        val accessToken = jwtMockManager.generateAccessToken(userId)

        // when
        mockMvc.delete("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }


        // then
        val user = userManagementPort.getUserNotNull(DomainId(userId))
        user.updatedAt shouldBeGreaterThan user.createdAt
    }
}
