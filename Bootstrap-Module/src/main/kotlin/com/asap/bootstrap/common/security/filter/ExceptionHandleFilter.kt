package com.asap.bootstrap.common.security.filter

import com.asap.bootstrap.common.exception.ExceptionResponse
import com.asap.common.exception.BusinessException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(0)
class ExceptionHandleFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    private val kotlinLogger = KotlinLogging.logger { }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handleException(e, response)
        }
    }

    private fun handleException(
        e: Exception,
        response: HttpServletResponse,
    ) {
        run {
            when (e) {
                is BusinessException -> {
                    kotlinLogger.info { "BusinessException: $e" }

                    response.status = e.httpStatus
                    response.outputStream.write(
                        objectMapper
                            .writeValueAsString(
                                ExceptionResponse.of(e),
                            ).toByteArray(),
                    )
                    response.contentType = "application/json"
                    // add cors header
                    response.addHeader("Access-Control-Allow-Origin", "*")
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                }

                else -> {
                    kotlinLogger.info { "unknown error occur: $e" }
                    response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    response.outputStream.write(
                        objectMapper
                            .writeValueAsString(
                                ExceptionResponse(
                                    message = e.message ?: "알 수 없는 오류가 발생했습니다.",
                                    code = "UNKNOWN-ERROR",
                                ),
                            ).toByteArray(),
                    )
                    response.contentType = "application/json"

                    response.addHeader("Access-Control-Allow-Origin", "*")
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                }
            }
        }
    }
}
