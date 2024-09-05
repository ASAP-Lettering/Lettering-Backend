package com.asap.bootstrap.common.security.annotation

import com.asap.bootstrap.common.security.vo.UserAuthentication
import com.asap.common.security.SecurityContextHolder
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AccessUserArgumentResolver: HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AccessUser::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val userAuthentication = SecurityContextHolder.getContext().getAuthentication() as UserAuthentication
        val userId = userAuthentication.getDetails()
        return userId
    }
}