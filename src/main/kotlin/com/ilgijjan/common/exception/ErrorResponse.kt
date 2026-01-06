package com.ilgijjan.common.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val status: HttpStatus,
    val code: String,
    val message: String,
    val errors: List<FieldErrorDetail>? = null
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status,
                code = errorCode.name,
                message = errorCode.message
            )
        }

        fun of(errorCode: ErrorCode, bindingResult: BindingResult): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status,
                code = errorCode.name,
                message = errorCode.message,
                errors = bindingResult.fieldErrors.map {
                    FieldErrorDetail(it.field, it.defaultMessage)
                }
            )
        }
    }
}
