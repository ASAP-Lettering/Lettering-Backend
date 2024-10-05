package com.asap.bootstrap.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.letter.dto.GenerateDraftKeyResponse
import com.asap.bootstrap.letter.dto.UpdateDraftLetterRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/letters/drafts")
interface DraftLetterApi {
    @Operation(summary = "임시 저장 키 발급")
    @PostMapping("/key")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "임시 저장 키 발급 성공"),
        ],
    )
    fun getDraftKey(
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

    @GetMapping()
    fun getAllDrafts(
        @AccessUser userId: String,
    )

    @DeleteMapping("/{draftId}")
    fun deleteDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String,
    )
}
