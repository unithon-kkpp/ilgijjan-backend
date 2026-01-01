package com.ilgijjan.common.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.common.exception.ErrorResponse
import com.ilgijjan.domain.auth.application.TokenManager
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val tokenManager: TokenManager
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            val token = jwtTokenProvider.resolveToken(request)

            if (token != null) {
                jwtTokenProvider.validateToken(token)
                tokenManager.validateNotBlacklisted(token)
                request.setAttribute("ACCESS_TOKEN", token)
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)

        } catch (e: CustomException) {
            setErrorResponse(response, e)
        }
    }

    private fun setErrorResponse(response: HttpServletResponse, e: CustomException) {
        response.status = e.errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponse.of(e.errorCode)

        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (ioException: IOException) {
            log.error("Filter Response Writer Error: {}", ioException.message, ioException)        }
    }
}
