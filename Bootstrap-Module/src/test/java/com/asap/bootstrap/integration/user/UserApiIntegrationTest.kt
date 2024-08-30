package com.asap.bootstrap.integration.user

import com.asap.bootstrap.user.dto.RegisterUserRequest
import com.asap.security.jwt.JwtTestConfig
import com.asap.security.jwt.TestJwtDataGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDate

/**
 * TODO: 요청 생성 util 클래스 추가하기
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JwtTestConfig::class)
class UserApiIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testJwtDataGenerator: TestJwtDataGenerator

    val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule())

    @Test
    fun registerUserSuccessTest() {
        // given
        val registerToken = testJwtDataGenerator.generateRegisterToken()
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now())
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
        val request = RegisterUserRequest(duplicateRegisterToken, true, true, true, LocalDate.now())
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
        val request = RegisterUserRequest(registerToken, true, true, true, LocalDate.now())
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
    fun registerUserInvalidTest_with_InvalidServicePermission() {
        //given
        val registerToken = testJwtDataGenerator.generateRegisterToken()
        val request = RegisterUserRequest(registerToken, false, true, true, LocalDate.now())
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
        val request = RegisterUserRequest(registerToken, true, false, true, LocalDate.now())
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