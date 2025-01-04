package com.asap.bootstrap.webhook

import com.asap.application.letter.port.`in`.LetterLogUsecase
import com.asap.bootstrap.webhook.dto.KakaoWebHookRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
class WebHookApi(
    private val letterLogUsecase: LetterLogUsecase,
    private val objectMapper: ObjectMapper
) {
    private val logger = KotlinLogging.logger {  }

    @PostMapping("/kakao")
    fun kakaoWebHook(
        @RequestHeader("Authorization") authorization: String,
        @RequestHeader("X-Kakao-Resource-ID") resourceId: String,
        @RequestHeader("User-Agent") userAgent: String,
        @RequestBody request: KakaoWebHookRequest
    ) {
        logger.info { "Authorization: $authorization, Resource Id: $resourceId, agent: $userAgent, request: $request" }

        letterLogUsecase.log(
            LetterLogUsecase.LogRequest(
                letterCode = request.requestId,
                logType = request.requestType,
                logContent = objectMapper.writeValueAsString(request)
            )
        )
    }
}