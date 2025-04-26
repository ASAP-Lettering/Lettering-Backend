package com.asap.domain.user.enums

import com.asap.common.exception.DefaultException

enum class SocialLoginProvider {
    KAKAO,
    GOOGLE,
    NAVER,
    ;

    companion object {
        fun parse(value: String): SocialLoginProvider =
            when (value) {
                entries.firstOrNull { it.name == value }?.name -> valueOf(value)
                else -> throw DefaultException.InvalidArgumentException("유효하지 않은 소셜 로그인 제공자입니다.")
            }
    }
}
