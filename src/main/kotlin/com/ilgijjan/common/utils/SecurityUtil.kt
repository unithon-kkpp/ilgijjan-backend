package com.ilgijjan.common.utils

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {
    fun getCurrentUserId(): Long {
        val principal = SecurityContextHolder.getContext().authentication?.principal

        if (principal !is Long) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        return principal
    }
}
