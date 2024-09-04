package com.asap.application.user.vo

import com.asap.common.security.Authentication

class UserAuthentication(
    val userId: String
):Authentication<String> {
    override fun getDetails(): String {
        return userId
    }

    override fun getName(): String {
        return userId
    }

}