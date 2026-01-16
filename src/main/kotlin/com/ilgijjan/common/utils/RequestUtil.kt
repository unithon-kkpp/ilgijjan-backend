package com.ilgijjan.common.utils

import jakarta.servlet.http.HttpServletRequest

object RequestUtil {
    fun getFullUri(request: HttpServletRequest): String {
        val queryString = request.queryString
        return if (queryString != null) "${request.requestURI}?$queryString" else request.requestURI
    }
}
