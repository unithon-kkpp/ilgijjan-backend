package com.ilgijjan.common.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            val token = jwtTokenProvider.resolveToken(request as HttpServletRequest)

            if (token != null) {
                jwtTokenProvider.validateToken(token)
                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            chain.doFilter(request, response)

        } catch (e: CustomException) {
            setErrorResponse(response as HttpServletResponse, e)
        }
    }

    private fun setErrorResponse(response: HttpServletResponse, e: CustomException) {
        val objectMapper = ObjectMapper()

        response.status = e.errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponse.of(e.errorCode)

        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}
