package com.ilgijjan.common.resolver

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(LoginUser::class.java) &&
                (parameter.parameterType == Long::class.javaObjectType || parameter.parameterType == Long::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || authentication.principal == "anonymousUser") {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        return authentication.principal.toString().toLong()
    }
}
