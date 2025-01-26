package com.asap.bootstrap.web.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.web.letter.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/letters/drafts")
interface DraftLetterApi {
    @Operation(summary = "임시 저장 키 발급")
    @PostMapping("/key")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 키 발급 성공",
                content = [
                    Content(
                        schema =
                            Schema(implementation = GenerateDraftKeyResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getDraftKey(
        @AccessUser userId: String,
    ): GenerateDraftKeyResponse

    @Operation(summary = "실물 편지 임시 저장 키 발급")
    @PostMapping("/physical/key")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 키 발급 성공",
                content = [
                    Content(
                        schema =
                            Schema(implementation = GenerateDraftKeyResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getPhysicalDraftKey(
        @AccessUser userId: String,
    ): GenerateDraftKeyResponse

    @Operation(summary = "임시 저장하기")
    @PostMapping("/{draftId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "임시 저장 성공"),
        ],
    )
    fun updateDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String,
        @RequestBody request: UpdateDraftLetterRequest,
    )

    @Operation(summary = "실물 편지 임시 저장하기")
    @PostMapping("/physical/{draftId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "임시 저장 성공"),
        ],
    )
    fun updatePhysicalDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String,
        @RequestBody request: UpdatePhysicalDraftLetterRequest,
    )

    @Operation(summary = "임시 저장 목록 조회")
    @GetMapping()
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 목록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = GetAllDraftLetterResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getAllDrafts(
        @AccessUser userId: String,
    ): GetAllDraftLetterResponse

    @Operation(summary = "실물 편지 임시 저장 목록 조회")
    @GetMapping("/physical")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 목록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = GetAllPhysicalDraftLetterResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getAllPhysicalDrafts(
        @AccessUser userId: String,
    ): GetAllPhysicalDraftLetterResponse

    @Operation(summary = "임시 저장 조회")
    @GetMapping("/{draftKey}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = GetDraftLetterResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getDraftLetter(
        @PathVariable draftKey: String,
        @AccessUser userId: String,
    ): GetDraftLetterResponse

    @Operation(summary = "실물 편지 임시 저장 조회")
    @GetMapping("/physical/{draftKey}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = GetPhysicalDraftLetterResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getPhysicalDraftLetter(
        @PathVariable draftKey: String,
        @AccessUser userId: String,
    ): GetPhysicalDraftLetterResponse

    @Operation(summary = "임시 저장 개수 조회")
    @GetMapping("/count")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "임시 저장 개수 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = GetDraftLetterCountResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getDraftCount(
        @AccessUser userId: String,
    ): GetDraftLetterCountResponse

    @Operation(summary = "임시 저장 삭제")
    @DeleteMapping("/{draftId}")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "임시 저장 삭제 성공"),
        ],
    )
    fun deleteDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String,
    )
}
