package com.asap.bootstrap.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.letter.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Letter", description = "Letter API")
@RequestMapping("/api/v1/letters")
interface LetterApi {


    @Operation(summary = "편지 열람 가능 검증")
    @PutMapping("/verify")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 열람 가능 검증 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LetterVerifyResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = """
                    LETTER-001 :편지가 존재하지 않음
                """
            ),
            ApiResponse(
                responseCode = "403",
                description = """
                    LETTER-002 :해당 사용자는 편지 열람 권한이 없음
                """
            )
        ]
    )
    fun verifyLetter(
        @RequestBody request: LetterVerifyRequest,
        @AccessUser userId: String
    ): LetterVerifyResponse


    /**
     * url 다시 논의해보기
     */
    @Operation(summary = "검증된 편지 열람")
    @GetMapping("/{letterId}/verify")
    fun getReceiveLetter(
        @PathVariable letterId: String
    ): ReceiveLetterInfoResponse


    @Operation(summary = "편지 수령 처리")
    @PostMapping("/receive/indirect")
    fun addReceiveLetter(
        @RequestBody request: AddIndirectLetterRequest
    )

    @Operation(summary = "실물 편지 내용 추가")
    @PostMapping("/receive/direct")
    fun addAnonymousLetter(
        @RequestBody request: AddDirectLetterRequest
    )


    @GetMapping("/independent")
    fun getIndependentLetters(
        @RequestParam page: Int,
        @RequestParam size: Int
    )


    @GetMapping("/{letterId}")
    fun getLetter(
        @PathVariable letterId: String
    )

    @PutMapping("/{letterId}")
    fun updateLetter(
        @PathVariable letterId: String,
    )

    @DeleteMapping("/{letterId}")
    fun deleteLetter(
        @PathVariable letterId: String
    )

    @Operation(summary = "편지 쓰기")
    @PostMapping("/send")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 전송 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SendLetterResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "4XX",
                description = "편지 전송 실패"
            )
        ]
    )
    fun sendLetter(
        @RequestBody request: SendLetterRequest,
        @AccessUser userId: String
    ): SendLetterResponse








}