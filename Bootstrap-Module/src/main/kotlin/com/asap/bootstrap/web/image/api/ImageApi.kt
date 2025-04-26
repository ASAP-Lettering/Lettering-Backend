package com.asap.bootstrap.web.image.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.web.image.dto.UploadImageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Image", description = "Image API")
@RequestMapping("/api/v1/images")
interface ImageApi {
    @Operation(
        summary = "이미지 업로드",
        description = "이미지를 업로드합니다.",
    )
    @PostMapping(consumes = ["multipart/form-data"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "이미지 업로드 성공",
                headers = [
                    Header(
                        name = "Authorization",
                        description = "액세스 토큰",
                        required = true,
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "4XX",
                description = "이미지 업로드 실패",
            ),
        ],
    )
    fun uploadImage(
        @RequestPart image: MultipartFile,
        @AccessUser userId: String,
    ): UploadImageResponse
}
