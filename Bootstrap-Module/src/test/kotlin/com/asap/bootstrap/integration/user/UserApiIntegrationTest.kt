package com.asap.bootstrap.integration.user

import com.asap.application.user.UserMockManager
import com.asap.application.user.exception.UserException
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.user.dto.RegisterUserRequest
import com.asap.security.jwt.TestJwtDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.time.LocalDate


class UserApiIntegrationTest: IntegrationSupporter() {


    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator
    @Autowired
    lateinit var userMockManager: UserMockManager

    @Test
    fun registerUserSuccessTest() {
        // given
        val registerToken = testJwtDataGenerator.generateRegisterToken()
        userMockManager.settingToken(registerToken)
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response = mockMvc.post("/api/v1/users") {
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
        val duplicateRegisterToken = testJwtDataGenerator.generateRegisterToken(
            socialId = "duplicate",
        )
        userMockManager.settingToken(duplicateRegisterToken)
        val request = RegisterUserRequest(duplicateRegisterToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response = mockMvc.post("/api/v1/users") {
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
        val registerToken = testJwtDataGenerator.generateInvalidToken()
        userMockManager.settingToken(registerToken)
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        // then
        response.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code"){
                exists()
                isString()
            }
        }
    }

    @Test
    fun registerUserInvalidTest_with_NonSavedRegisterToken() {
        // given
        val registerToken = testJwtDataGenerator.generateInvalidToken()
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now(), "realName")
        // when
        val response = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        // then
        response.andExpect {
            status { isUnauthorized() }
            jsonPath("$.code"){
                exists()
                isString()
                value(UserException.UserPermissionDeniedException().code)
            }
        }
    }

    @Test
    fun registerUserInvalidTest_with_InvalidServicePermission() {
        //given
        val registerToken = testJwtDataGenerator.generateRegisterToken()
        userMockManager.settingToken(registerToken)
        val request = RegisterUserRequest(registerToken, false, true, true, LocalDate.now(), "realName")
        //when
        val response = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        //then
        response.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun registerUserInvalidTest_with_InvalidPrivatePermission() {
        //given
        val registerToken = testJwtDataGenerator.generateRegisterToken()
        userMockManager.settingToken(registerToken)
        val request = RegisterUserRequest(registerToken, true, false, true, LocalDate.now(), "realName")
        //when
        val response = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        //then
        response.andExpect {
            status { isBadRequest() }
        }
    }
}