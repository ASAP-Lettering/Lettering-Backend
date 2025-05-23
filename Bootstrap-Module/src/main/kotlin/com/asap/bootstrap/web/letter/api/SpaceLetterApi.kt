package com.asap.bootstrap.web.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.web.letter.dto.GetSpaceLetterDetailResponse
import com.asap.bootstrap.web.letter.dto.GetSpaceLettersResponse
import com.asap.bootstrap.web.letter.dto.ModifyLetterRequest
import com.asap.bootstrap.web.letter.dto.MoveLetterToSpaceRequest
import com.asap.common.page.PageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "SpaceLetter", description = "SpaceLetter API")
@RequestMapping("/api/v1/spaces")
interface SpaceLetterApi {
    @Operation(summary = "행성 편지 목록 조회")
    @GetMapping("/{spaceId}/letters")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "행성 편지 목록 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PageResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getAllSpaceLetters(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @PathVariable spaceId: String,
        @AccessUser userId: String,
    ): PageResponse<GetSpaceLettersResponse>

    // TODO: LetterApi로 이동
    @Operation(summary = "궤도 편지 행성으로 이동")
    @PutMapping("/letters/{letterId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 이동 성공",
            ),
        ],
    )
    fun moveLetterToSpace(
        @PathVariable letterId: String,
        @RequestBody request: MoveLetterToSpaceRequest,
        @AccessUser userId: String,
    )

    @Operation(summary = "행성 편지 독립 편지로 이동")
    @PutMapping("/letters/{letterId}/independent")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 이동 성공",
            ),
        ],
    )
    fun moveLetterToIndependentLetter(
        @PathVariable letterId: String,
        @AccessUser userId: String,
    )

    @Operation(summary = "행성 편지 상세 조회")
    @GetMapping("/letters/{letterId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 상세 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GetSpaceLetterDetailResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getSpaceLetterDetail(
        @PathVariable letterId: String,
        @AccessUser userId: String,
    ): GetSpaceLetterDetailResponse

    @Operation(summary = "행성 편지 삭제")
    @DeleteMapping("/letters/{letterId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 삭제 성공",
            ),
        ],
    )
    fun deleteSpaceLetter(
        @PathVariable letterId: String,
        @AccessUser userId: String,
    )

    @PutMapping("/letters/{letterId}/content")
    fun updateSpaceLetter(
        @PathVariable letterId: String,
        @RequestBody request: ModifyLetterRequest,
        @AccessUser userId: String,
    )
}
