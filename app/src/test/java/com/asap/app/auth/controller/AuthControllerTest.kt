package com.asap.app.auth.controller

import com.asap.app.auth.dto.SocialLoginRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper: ObjectMapper = ObjectMapper()


    @Test
    fun socialLoginSuccessTest(){
        // given
        val request = SocialLoginRequest("registered")
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", "kakao") {
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
    fun socialLoginNonRegisteredTest(){
        // given
        val request = SocialLoginRequest("nonRegistered")
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", "kakao") {
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
    fun socialLoginBadRequestTest(){
        // given
        val request = SocialLoginRequest("invalid")
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", "kakao") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        // then
        response.andExpect {
            status { isBadRequest() }
        }
    }


}