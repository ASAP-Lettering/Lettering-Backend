package com.asap.bootstrap.integration.letter

import com.asap.application.letter.LetterMockManager
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.webhook.dto.KakaoChatType
import com.asap.bootstrap.webhook.dto.KakaoWebHookRequest
import com.asap.domain.letter.entity.LetterLogType
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class LetterLogApiIntegrationTest(
    private val letterMockManager: LetterMockManager
) : IntegrationSupporter() {

    @Test
    fun getLetterShareStatus_success() {
        //given
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val userId = userMockManager.settingUser(username = "username")
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val letter =
            letterMockManager.generateMockSendLetter("username", senderId = senderId)
        val letterCode = letter.letterCode!!


        val request = KakaoWebHookRequest(
            requestId = letterCode,
            requestType = LetterLogType.SHARE,
            chatType = KakaoChatType.MEMO_CHAT,
            hashChatId = "1234567890",
            templateId = "1234567890"
        )

        mockMvc.post("/webhook/kakao") {
            headers {
                set("Authorization", "KakaoAP 1234567890")
                set("X-Kakao-Resource-ID", "1234567890")
                set("User-Agent", "KakaoAgent")
            }
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
        //when then
        mockMvc.get("/api/v1/letters/logs/share/status?letterCode=$letterCode") {
            header("Authorization", "Bearer $accessToken")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.isShared") { value(true) }
                jsonPath("$.letterId") { value(letter.id.value) }
                jsonPath("$.shareTarget") { value(request.chatType.name) }
            }
    }

    @Test
    fun getLetterShareStatus_fail() {
        //given
        val senderId = userMockManager.settingUser(username = "senderUsername")
        val userId = userMockManager.settingUser(username = "username")
        val accessToken = jwtMockManager.generateAccessToken(userId)
        val letter =
            letterMockManager.generateMockSendLetter("username", senderId = senderId)
        val letterCode = letter.letterCode!!

        //when then
        mockMvc.get("/api/v1/letters/logs/share/status?letterCode=$letterCode") {
            header("Authorization", "Bearer $accessToken")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.isShared") { value(false) }
                jsonPath("$.letterId") { value(null) }
                jsonPath("$.shareTarget") { value(null) }
            }
    }
}