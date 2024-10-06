package com.asap.application.letter.exception

import com.asap.common.exception.BusinessException

sealed class LetterException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = 400,
    message: String = DEFAULT_ERROR_MESSAGE,
) : BusinessException(codePrefix, errorCode, httpStatus, message) {
    class SendLetterNotFoundException(
        message: String = "존재하지 않는 편지입니다.",
    ) : LetterException(
            errorCode = 1,
            message = message,
        )

    class InvalidLetterAccessException(
        message: String = "편지에 대한 접근 권한이 없습니다.",
    ) : LetterException(
            errorCode = 2,
            message = message,
            httpStatus = 403,
        )

    class ReceiveLetterNotFoundException(
        message: String = "존재하지 않는 편지입니다.",
    ) : LetterException(
            errorCode = 3,
            message = message,
        )

    class DraftLetterNotFoundException(
        message: String = "존재하지 않는 임시 편지입니다.",
    ) : LetterException(
            errorCode = 4,
            message = message,
            httpStatus = 404,
        )

    companion object {
        const val CODE_PREFIX = "LETTER"
        const val DEFAULT_ERROR_MESSAGE = "편지 관련된 예외가 발생했습니다."
    }
}
