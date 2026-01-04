package com.ilgijjan.common.exception

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status,
                code = errorCode.name,
                message = errorCode.message
            )
        }

        fun of(errorCode: ErrorCode, detailMessage: String): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status,
                code = errorCode.name,
                message = detailMessage
            )
        }
    }
}
