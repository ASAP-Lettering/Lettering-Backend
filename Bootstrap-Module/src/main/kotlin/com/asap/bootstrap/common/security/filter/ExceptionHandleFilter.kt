package com.asap.bootstrap.common.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(0)
class ExceptionHandleFilter(

): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }
}