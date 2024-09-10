package com.asap.bootstrap.image.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.image.dto.UploadImageResponse
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/v1/images")
interface ImageApi {

    @PostMapping()
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "이미지 업로드 성공",
                headers = [
                    Header(
                        name = "Authorization",
                        description = "액세스 토큰",
                        required = true
                    )
                ]
            ),
            ApiResponse(
                responseCode = "4XX",
                description = "이미지 업로드 실패"
            )
        ]
    )
    fun uploadImage(
        @RequestPart image: MultipartFile,
        @AccessUser userId: String
    ): UploadImageResponse
}