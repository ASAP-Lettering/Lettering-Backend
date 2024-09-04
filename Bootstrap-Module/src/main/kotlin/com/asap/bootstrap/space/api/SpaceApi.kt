package com.asap.bootstrap.space.api

import com.asap.bootstrap.space.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Space", description = "Space API")
@RequestMapping("/api/v1/spaces")
interface SpaceApi {


    @Operation(summary = "메인 스페이스 ID 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "메인 스페이스 ID 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MainSpaceInfoResponse::class)
                    )
                ]
            )
        ]
    )
    @GetMapping("/main")
    fun getMainSpace(): MainSpaceInfoResponse


    @Operation(summary = "전체 스페이스 목록 조회")
    @GetMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "전체 스페이스 목록 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GetAllSpaceResponse::class)
                    )
                ]
            )
        ]
    )
    fun getSpaces(): GetAllSpaceResponse

    @Operation(summary = "스페이스 생성")
    @PostMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "스페이스 생성 성공"
            )
        ]
    )
    fun createSpace(
        @RequestBody request: CreateSpaceRequest
    )

    @Operation(summary = "스페이스 이름 수정")
    @PutMapping("/{spaceId}/name")
    @ApiResponses(
        value =[
            ApiResponse(
                responseCode = "200",
                description = "스페이스 이름 수정 성공"
            )
        ]
    )
    fun updateSpaceName(
        @PathVariable spaceId: String,
        @RequestBody request: UpdateSpaceNameRequest
    )

    @Operation(summary = "스페이스 삭제")
    @DeleteMapping("/{spaceId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "스페이스 삭제 성공"
            )
        ]
    )
    fun deleteSpace(
        @PathVariable spaceId: String
    )

    @Operation(summary = "스페이스 순서 변경")
    @PutMapping("/order")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "스페이스 순서 변경 성공"
            )
        ]
    )
    fun updateSpaceOrder(
        @RequestBody request: UpdateSpaceOrderRequest
    )

    @Operation(summary = "여러 스페이스 삭제")
    @DeleteMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여러 스페이스 삭제 성공"
            )
        ]
    )
    fun deleteSpaces(
        @RequestBody request: DeleteMultipleSpacesRequest
    )




}