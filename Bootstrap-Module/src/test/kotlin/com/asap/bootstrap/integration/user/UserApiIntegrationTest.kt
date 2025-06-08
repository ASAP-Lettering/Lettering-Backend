package com.asap.bootstrap.integration.user

import com.asap.application.letter.LetterMockManager
import com.asap.application.letter.port.out.SendLetterManagementPort
import com.asap.application.user.exception.UserException
import com.asap.application.user.port.out.UserManagementPort
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.web.user.dto.LogoutRequest
import com.asap.bootstrap.web.user.dto.RegisterUserRequest
import com.asap.bootstrap.web.user.dto.UnregisterUserRequest
import com.asap.bootstrap.web.user.dto.UpdateBirthdayRequest
import com.asap.domain.common.DomainId
import com.asap.domain.letter.entity.SendLetter
import com.asap.domain.letter.enums.LetterStatus
import com.asap.domain.letter.vo.LetterContent
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
    private val userManagementPort: UserManagementPort,
    private val letterMockManager: LetterMockManager,
    private val sendLetterManagementPort: SendLetterManagementPort,
) : IntegrationSupporter() {
    @Test
    fun registerUserSuccessTest() {
        // given
        val registerToken = jwtMockManager.generateRegisterToken()
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName", null)
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
        val request = RegisterUserRequest(duplicateRegisterToken, true, true, true, LocalDate.now(), "realName", null)
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
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName", null)
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
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName", null)
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
        val request = RegisterUserRequest(registerToken, false, true, true, LocalDate.now(), "realName", null)
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
        val request = RegisterUserRequest(registerToken, true, false, true, LocalDate.now(), "realName", null)
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
    inner class DeleteUser {
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
        fun deleteUser_with_reason() {
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
        val beforeUpdatedAt = userManagementPort.getUserNotNull(DomainId(userId)).updatedAt
        // when
        mockMvc.delete("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        // then
        val afterUpdatedAt = userManagementPort.getUserNotNull(DomainId(userId)).updatedAt
        afterUpdatedAt shouldBeGreaterThan beforeUpdatedAt
    }

    @Test
    fun registerUserWithAnonymousSendLetterCodeTest() {
        // given
        // Create an anonymous letter
        val letterContent =
            LetterContent(
                content = "anonymous letter content",
                templateType = 1,
                images = mutableListOf("image1", "image2"),
            )
        val receiverName = "testReceiver"
        val anonymousSendLetter =
            SendLetter.createAnonymous(
                content = letterContent,
                receiverName = receiverName,
                letterCode = "test-letter-code",
                senderName = "Anonymous",
            )
        sendLetterManagementPort.save(anonymousSendLetter)

        val letterCode = anonymousSendLetter.letterCode!!

        // Register a user with the anonymous letter code
        val registerToken = jwtMockManager.generateRegisterToken()
        val request =
            RegisterUserRequest(
                registerToken = registerToken,
                servicePermission = true,
                privatePermission = true,
                marketingPermission = true,
                birthday = LocalDate.now(),
                realName = receiverName,
                anonymousSendLetterCode = letterCode,
            )

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

        // Verify that the letter is properly associated with the user
        val updatedLetter = sendLetterManagementPort.getLetterByCodeNotNull(letterCode)
        updatedLetter.senderId shouldNotBe null
    }
}
