package com.asap.bootstrap.web.letter.controller

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.bootstrap.web.letter.api.LetterLogApi
import com.asap.bootstrap.web.letter.dto.LetterShareStatusResponse
import com.asap.bootstrap.webhook.dto.KakaoWebHookRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.RestController

@RestController
class LetterLogController(
    private val letterLogUsecase: LetterLogUsecase,
    private val objectMapper: ObjectMapper
) : LetterLogApi {
    override fun getLetterShareStatus(letterCode: String, userId: String): LetterShareStatusResponse {
        return letterLogUsecase.finLatestLogByLetterCode(letterCode)?.let {
            val kakaoWebHookRequest =
                objectMapper.readValue(it.logContent, KakaoWebHookRequest::class.java)
            LetterShareStatusResponse.success(
                letterId = it.letterId,
                shareTarget = kakaoWebHookRequest.chatType
            )
        } ?: run {
            LetterShareStatusResponse.fail()
        }
    }
}