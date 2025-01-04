package com.asap.bootstrap.web.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.webhook.dto.KakaoChatType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Letter", description = "Letter API")
@RequestMapping("/api/v1/letters/logs")
interface LetterLogApi {

    @Operation(summary = "편지 공유 상태 조회")
    @GetMapping("/share/status")
    @ApiResponses(
        value =[
            ApiResponse(
                responseCode = "200",
                description = """
                    편지 공유 상태 조회 성공
                    - isShared: 공유 여부
                    - letterId: 편지 ID
                    - shareTarget: 공유 대상
                    
                    isShared가 true인 경우, letterId와 shareTarget은 null이 아님 즉 공유 성공
                    isShared가 false인 경우, letterId와 shareTarget은 null임 즉 공유 실패
                    
                    shareTarget은 카카오 챗 타입
                    * MEMO_CHAT: 개인 채팅
                    * DIRECT_CHAT: 1:1 채팅
                    * MULTI_CHAT: 그룹 채팅
                    * OPEN_DIRECT_CHAT: 오픈 채팅
                    * OPEN_MULTI_CHAT: 오픈 그룹 채팅
                """,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LetterShareStatusResponse::class)
                    )
                ]
            )
        ]
    )
    fun getLetterShareStatus(
        @RequestParam("letterCode") letterCode: String,
        @AccessUser userId: String
    ): LetterShareStatusResponse
}


data class LetterShareStatusResponse(
    val isShared: Boolean,
    val letterId: String?,
    val shareTarget: KakaoChatType?,
) {
    companion object{
        fun success(
            letterId: String,
            shareTarget: KakaoChatType
        ): LetterShareStatusResponse {
            return LetterShareStatusResponse(
                isShared = true,
                letterId = letterId,
                shareTarget = shareTarget
            )
        }

        fun fail(): LetterShareStatusResponse {
            return LetterShareStatusResponse(
                isShared = false,
                letterId = null,
                shareTarget = null
            )
        }
    }
}