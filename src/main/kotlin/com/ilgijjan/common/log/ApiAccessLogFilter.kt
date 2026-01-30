package com.ilgijjan.common.log

import com.ilgijjan.common.utils.IdGenerator
import com.ilgijjan.common.utils.RequestUtil
import com.ilgijjan.common.utils.StringUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ApiAccessLogFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val startTime = System.currentTimeMillis()
        MDC.put("requestId", IdGenerator.generateTraceId())

        val wrappingRequest = ReadableRequestWrapper(request)
        val wrappingResponse = ContentCachingResponseWrapper(response)

        val reqBody = StringUtil.removeWhitespaces(StringUtil.maskSensitiveFields(wrappingRequest.getBody()))

        log.info(">>> [REQ] method={}, uri={}, body={}",
            request.method, RequestUtil.getFullUri(request), reqBody)

        try {
            chain.doFilter(wrappingRequest, wrappingResponse)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            val userId = MDC.get("userId") ?: "guest"
            val resBody = String(wrappingResponse.contentAsByteArray, Charsets.UTF_8)
            val refinedResBody = StringUtil.maskSensitiveFields(resBody).let {
                if (it.length > 1000) it.substring(0, 1000) + "...(truncated)" else it
            }

            log.info("<<< [RES] [User:{}] status={}, duration={}ms, body={}",
                userId, response.status, duration, refinedResBody)

            wrappingResponse.copyBodyToResponse()
            MDC.clear()
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/error")
    }
}
