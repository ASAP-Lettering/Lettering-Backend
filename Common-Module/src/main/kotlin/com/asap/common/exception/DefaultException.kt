package com.asap.common.exception

sealed class DefaultException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = 400,
    message: String = DEFAULT_ERROR_MESSAGE
): BusinessException(codePrefix, errorCode, httpStatus, message) {

    class InvalidDefaultException(message: String = "유효하지 않은 프로퍼티입니다." ): DefaultException(
        errorCode = 1,
        message = message
    )

    class InvalidArgumentException(message: String = "유효하지 않은 값입니다." ): DefaultException(
        errorCode = 2,
        message = message
    )

    class InvalidStateException(message: String = "유효하지 않은 상태입니다." ): DefaultException(
        errorCode = 3,
        httpStatus = 500,
        message = message
    )

    companion object{
        const val CODE_PREFIX = "DEFAULT"
        const val DEFAULT_ERROR_MESSAGE = "프로퍼티와 관련된 예외가 발생했습니다."
    }
}