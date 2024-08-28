package com.asap.common.exception

abstract class BusinessException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = DEFAULT_HTTP_STATUS,
    override val message: String = DEFAULT_ERROR_MESSAGE
) : RuntimeException(message){

    val code: String = "$codePrefix-${errorCode.toString().padStart(DEFAULT_CODE_LENGTH, DEFAULT_CODE_PAD)}"
    val httpStatus: Int = httpStatus

    companion object{
        const val CODE_PREFIX = "UNEXPECTED"
        const val DEFAULT_ERROR_MESSAGE = "예상하지 못한 예외가 발생했습니다."
        const val DEFAULT_HTTP_STATUS = 500
        const val DEFAULT_CODE_LENGTH = 3
        const val DEFAULT_CODE_PAD = '0'
    }
}