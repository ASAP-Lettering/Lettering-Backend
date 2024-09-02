package com.asap.bootstrap.common

import com.asap.common.exception.BusinessException
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "예외 응답")
data class ExceptionResponse(
    @Schema(description = "예외 메시지")
    val message: String,
    @Schema(description = "예외 코드, ex) USER-001")
    val code: String
) {

    companion object{
        fun of(businessException: BusinessException): ExceptionResponse{
            return ExceptionResponse(businessException.message, businessException.code)
        }
    }
}