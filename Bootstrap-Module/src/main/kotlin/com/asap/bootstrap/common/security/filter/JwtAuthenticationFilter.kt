package com.asap.bootstrap.common.security.filter

import com.asap.application.user.port.`in`.TokenResolveUsecase
import com.asap.bootstrap.common.security.vo.UserAuthentication
import com.asap.common.security.SecurityContext
import com.asap.common.security.SecurityContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1)
class JwtAuthenticationFilter(
    private val tokenResolveUsecase: TokenResolveUsecase
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(AUTHORIZATION_HEADER)
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            val accessToken = token.substring(BEARER_PREFIX.length)
            val resolveResponse = tokenResolveUsecase.resolveAccessToken(accessToken)
            SecurityContextHolder.setContext(
                SecurityContext(UserAuthentication(resolveResponse.userId))
            )
        }

        val filterResponse = filterChain.doFilter(request, response)

        SecurityContextHolder.clearContext()

        return filterResponse
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}