package com.asap.bootstrap.acceptance.letter.controller

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.bootstrap.acceptance.letter.LetterAcceptanceSupporter
import com.asap.bootstrap.webhook.dto.KakaoChatType
import com.asap.bootstrap.webhook.dto.KakaoWebHookRequest
import com.asap.domain.letter.entity.LetterLogType
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.get

class LetterLogControllerTest: LetterAcceptanceSupporter() {

    @MockBean
    private lateinit var letterLogUsecase: LetterLogUsecase

    @Test
    fun getLetterShareStatus(){
        // given
        val letterCode = "letterCode"
        val accessToken = jwtMockManager.generateAccessToken("userId")

        BDDMockito.given(letterLogUsecase.finLatestLogByLetterCode(letterCode)).willReturn(
            LetterLogUsecase.LogResponse(
                letterId = "letterId",
                logType = LetterLogType.SHARE,
                logContent = KakaoWebHookRequest(
                    chatType = KakaoChatType.MEMO_CHAT,
                    hashChatId = "hashChatId",
                    templateId = "templateId",
                    requestType = LetterLogType.SHARE,
                    requestId = "requestId",
                ).let{
                    objectMapper.writeValueAsString(it)
                }
            )
        )

        // when then
        mockMvc.get("/api/v1/letters/logs/share/status?letterCode=$letterCode"){
            header("Authorization", "Bearer $accessToken")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.isShared") { isBoolean() }
                jsonPath("$.letterId") { isString() }
                jsonPath("$.shareTarget") { isString() }
            }
    }
}