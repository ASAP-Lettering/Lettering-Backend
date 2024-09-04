package com.asap.application.user.utils

import com.asap.common.security.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserUtils {

    /**
     * TODO: 더 좋은 방법 없는지 고민해보기
     */
    fun getAccessUserId(): String {
        return SecurityContextHolder.getContext().getAuthentication().getDetails().toString()
    }
}