package com.asap.application.user.exception

import com.asap.common.exception.BusinessException

sealed class UserException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = 400,
    message: String = DEFAULT_ERROR_MESSAGE
): BusinessException(codePrefix, errorCode, httpStatus, message) {

    class UserAlreadyRegisteredException: UserException(
        errorCode = 1,
        message = "이미 가입된 사용자입니다."
    )


    class UserAuthNotFoundException: UserException(
        errorCode = 2,
        message = "사용자 인증 정보를 찾을 수 없습니다."
    )

    class UserNotFoundException: UserException(
        errorCode = 3,
        message = "사용자를 찾을 수 없습니다."
    )


    companion object{
        const val CODE_PREFIX = "USER"
        const val DEFAULT_ERROR_MESSAGE = "사용자와 관련된 예외가 발생했습니다."
    }
}