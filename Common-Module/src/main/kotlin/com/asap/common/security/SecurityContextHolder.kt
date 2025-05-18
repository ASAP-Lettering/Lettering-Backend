package com.asap.common.security

class SecurityContextHolder {
    companion object {
        private val contextHolder = ThreadLocal<SecurityContext<*, *>>()

        fun getContext(): SecurityContext<*, *>? = contextHolder.get()

        fun setContext(context: SecurityContext<*, *>) {
            contextHolder.set(context)
        }

        fun clearContext() {
            contextHolder.remove()
        }
    }
}
