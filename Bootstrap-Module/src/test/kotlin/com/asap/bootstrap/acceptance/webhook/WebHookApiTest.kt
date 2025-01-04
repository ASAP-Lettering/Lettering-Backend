package com.asap.bootstrap.acceptance.webhook

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.bootstrap.AcceptanceSupporter
import com.asap.domain.letter.entity.LetterLogType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class WebHookApiTest : AcceptanceSupporter() {

    @MockBean
    private lateinit var letterLogUsecase: LetterLogUsecase

    @Test
    fun kakaoWebHook() {
        //given
        val request = mapOf(
            "requestId" to "1234567890",
            "requestType" to LetterLogType.SHARE.name,
            "CHAT_TYPE" to "MemoChat",
            "HASH_CHAT_ID" to "1234567890",
            "TEMPLATE_ID" to "1234567890"
        )

        //when then
        mockMvc.post("/webhook/kakao") {
            headers {
                set("Authorization", "KakaoAP 1234567890")
                set("X-Kakao-Resource-ID", "1234567890")
                set("User-Agent", "KakaoAgent")
            }
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }
}