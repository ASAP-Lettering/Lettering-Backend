package com.asap.bootstrap.integration.auth

import com.asap.bootstrap.auth.dto.SocialLoginRequest
import com.asap.client.KakaoTestData
import com.asap.client.TestWebClientConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.Test


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestWebClientConfig::class)
class AuthApiIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mockWebServer: MockWebServer

    val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule())


    @Test
    fun socialLoginSuccessTest() {
        // given
        val request = SocialLoginRequest("registered")
        val provider = "KAKAO"
        mockWebServer.enqueue(KakaoTestData.KAKAO_OAUTH_SUCCESS_RESPONSE)
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", provider) {
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
        val response = mockMvc.post("/api/v1/auth/login/{provider}", provider) {
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
        }.andDo {
            print()
        }
    }

    @Test
    fun socialLoginBadRequestTest_with_invalid_access_token() {
        // given
        val request = SocialLoginRequest("invalid")
        val provider = "KAKAO"
        mockWebServer.enqueue(KakaoTestData.KAKAO_OAUTH_FAIL_RESPONSE_WITH_INVALID_ACCESS_TOKEN)
        // when
        val response = mockMvc.post("/api/v1/auth/login/{provider}", provider) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        // then
        response.andExpect {
            status { isBadRequest() }
        }
    }

}