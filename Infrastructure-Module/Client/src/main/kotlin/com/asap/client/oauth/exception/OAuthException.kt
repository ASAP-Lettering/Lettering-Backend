package com.asap.client.oauth.exception

import com.asap.common.exception.BusinessException

sealed class OAuthException(
    codePrefix: String = CODE_PREFIX,
    errorCode: Int,
    httpStatus: Int = 400,
    message: String = DEFAULT_ERROR_MESSAGE
): BusinessException(codePrefix, errorCode, httpStatus, message) {

    class OAuthRetrieveFailedException(
        message: String = "OAuth 정보를 가져오는데 실패했습니다."
    ): OAuthException(
        errorCode = 1,
        message = message
    )

    companion object{
        const val CODE_PREFIX = "OAUTH"
        const val DEFAULT_ERROR_MESSAGE = "OAuth 관련 예외가 발생했습니다."
    }
}