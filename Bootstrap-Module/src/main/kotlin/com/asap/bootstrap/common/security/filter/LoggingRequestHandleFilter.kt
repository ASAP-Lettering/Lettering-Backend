package com.asap.bootstrap.common.security.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(-1)
class LoggingRequestHandleFilter : OncePerRequestFilter() {
    private val kotlinLogger = KotlinLogging.logger { }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        kotlinLogger.info { "Request URI: ${request.requestURI} " }
        filterChain.doFilter(request, response)
    }
}
