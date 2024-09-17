package com.asap.bootstrap.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/spaces")
interface SpaceLetterApi {

    @GetMapping("/{spaceId}/letters")
    fun getSpaceLetters(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @PathVariable spaceId: String
    )


    @Operation(summary = "궤도 편지 행성으로 이동")
    @PutMapping("/letters/{letterId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "편지 이동 성공"
            ),
        ]
    )
    fun moveLetterToSpace(
        @PathVariable letterId: String,
        @RequestBody request: MoveLetterToSpaceRequest,
        @AccessUser userId: String
    )


    @Operation(summary = "행성 편지 독립 편지로 이동")
    @PutMapping("/letters/{letterId}/independent")
    fun moveLetterToIndependentLetter(
        @PathVariable letterId: String,
        @AccessUser userId: String
    )
}