package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.application.user.UserMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.letter.dto.LetterVerifyRequest
import com.asap.bootstrap.letter.dto.SendLetterRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class LetterApiIntegrationTest : IntegrationSupporter() {

    @Autowired
    lateinit var userMockManager: UserMockManager

    @Autowired
    lateinit var letterMockManager: LetterMockManager


    @Nested
    inner class LetterVerify {
        @Test
        @DisplayName("편지 열람 가능 검증 성공")
        fun verifyLetter() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            userMockManager.settingToken(accessToken)
            val letterCode = letterMockManager.generateMockSendLetter("username")
            val request = LetterVerifyRequest(letterCode)
            //when
            val response = mockMvc.put("/api/v1/letters/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
            //then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
        }

        @Test
        @DisplayName("편지가 존재하지 않음")
        fun verifyLetter_With_InvalidLetterCode() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            userMockManager.settingToken(accessToken)
            val request = LetterVerifyRequest("invalidLetterCode")
            //when
            val response = mockMvc.put("/api/v1/letters/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
            //then
            response.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") {
                    value("LETTER-001")
                }
            }
        }

        @Test
        @DisplayName("해당 사용자는 편지 열람 권한이 없음")
        fun verifyLetter_With_InvalidUser() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            userMockManager.settingToken(accessToken)
            val letterCode = letterMockManager.generateMockSendLetter("otherUsername")
            val request = LetterVerifyRequest(letterCode)
            //when
            val response = mockMvc.put("/api/v1/letters/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
            //then
            response.andExpect {
                status { isForbidden() }
                jsonPath("$.code") {
                    value("LETTER-002")
                }
            }
        }

        @Test
        @DisplayName("다른 사용자가 이미 연람함 편지면 열람 불가")
        fun verifyLetter_With_ExpiredLetter() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            userMockManager.settingToken(accessToken)
            val letterCode = letterMockManager.generateMockExpiredSendLetter("username", "otherUserId")
            val request = LetterVerifyRequest(letterCode)
            //when
            val response = mockMvc.put("/api/v1/letters/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
            //then
            response.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") {
                    value("LETTER-001")
                }
            }
        }

        @Test
        @DisplayName("이전에 열람한 적이 있다면 다시 열람 가능")
        fun verifyLetter_With_ExpiredLetter_ReAccessible() {
            //given
            val userId = userMockManager.settingUser(username = "username")
            val accessToken = testJwtDataGenerator.generateAccessToken(userId)
            userMockManager.settingToken(accessToken)
            val letterCode = letterMockManager.generateMockExpiredSendLetter("username", userId)
            val request = LetterVerifyRequest(letterCode)
            //when
            val response = mockMvc.put("/api/v1/letters/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Authorization", "Bearer $accessToken")
            }
            //then
            response.andExpect {
                status { isOk() }
                jsonPath("$.letterId") {
                    exists()
                    isString()
                    isNotEmpty()
                }
            }
        }


    }


    @Test
    fun sendLetter() {
        //given
        val request = SendLetterRequest(
            receiverName = "receiverName",
            content = "content",
            images = listOf("images"),
            templateType = 1,
            draftId = "draftId"
        )
        val userId = userMockManager.settingUser()
        val accessToken = testJwtDataGenerator.generateAccessToken(userId)
        userMockManager.settingToken(accessToken)
        //when
        val response = mockMvc.post("/api/v1/letters/send") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Authorization", "Bearer $accessToken")
        }
        //then
        response.andExpect {
            status { isOk() }
            jsonPath("$.letterCode") {
                exists()
                isString()
                isNotEmpty()
            }
        }
    }
}