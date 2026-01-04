package com.ilgijjan.common.utils

import com.ilgijjan.common.constants.AuthConstants
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object SecurityUtil {
    fun getCurrentUserId(): Long {
        val principal = SecurityContextHolder.getContext().authentication?.principal

        if (principal !is Long) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        return principal
    }

    fun getCurrentAccessToken(): String {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val token = request.getAttribute(AuthConstants.ACCESS_TOKEN_ATTRIBUTE) as? String ?: throw CustomException(ErrorCode.MISSING_AUTH_TOKEN)
        return token
    }
}
