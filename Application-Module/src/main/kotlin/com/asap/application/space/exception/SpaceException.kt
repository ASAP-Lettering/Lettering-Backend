package com.asap.application.space.exception

import com.asap.common.exception.BusinessException

sealed class SpaceException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = 400,
    message: String = DEFAULT_ERROR_MESSAGE,
) : BusinessException(codePrefix, errorCode, httpStatus, message) {
    class InvalidSpaceUpdateException(
        message: String = "유효하지 않는 스페이스 순서 변경 요청입니다.",
    ) : SpaceException(
            errorCode = 1,
            message = message,
        )

    class SpaceNotFoundException(
        message: String = "해당 스페이스를 찾을 수 없습니다.",
    ) : SpaceException(
            errorCode = 2,
            message = message,
        )

    companion object {
        const val CODE_PREFIX = "SPACE"
        const val DEFAULT_ERROR_MESSAGE = "스페이스 관련된 예외가 발생했습니다."
    }
}
