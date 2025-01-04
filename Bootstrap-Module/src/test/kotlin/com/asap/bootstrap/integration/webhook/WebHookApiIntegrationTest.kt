package com.asap.bootstrap.integration.webhook

import com.asap.application.letter.LetterMockManager
import com.asap.application.letter.port.out.LetterLogManagementPort
import com.asap.bootstrap.IntegrationSupporter
import com.asap.bootstrap.webhook.dto.KakaoChatType
import com.asap.bootstrap.webhook.dto.KakaoWebHookRequest
import com.asap.domain.letter.entity.LetterLogType
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class WebHookApiIntegrationTest(
    private val letterLogManagementPort: LetterLogManagementPort,
    private val letterMockManager: LetterMockManager
) : IntegrationSupporter() {

    @Test
    fun kakaoWebHook() {
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

        //when
        val response = mockMvc.post("/webhook/kakao") {
            headers {
                set("Authorization", "KakaoAP 1234567890")
                set("X-Kakao-Resource-ID", "1234567890")
                set("User-Agent", "KakaoAgent")
            }
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        // then
        response.andExpect {
            status { isOk() }
        }
        with(letterLogManagementPort.findAll().first()) {
            this.targetLetterId shouldBe letter.id
            this.logType shouldBe LetterLogType.SHARE
            objectMapper.readValue<KakaoWebHookRequest>(this.content) shouldBe request
        }
    }
}