package com.asap.common.security

class SecurityContext<T: Authentication<R>, R>(
    private var authentication: T
) {

    fun getAuthentication(): T {
        return authentication
    }

}