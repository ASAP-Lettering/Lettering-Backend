package com.asap.bootstrap.common

import com.asap.common.exception.BusinessException


data class ExceptionResponse(
    val message: String,
    val code: String
) {

    companion object{
        fun of(businessException: BusinessException): ExceptionResponse{
            return ExceptionResponse(businessException.message, businessException.code)
        }
    }
}