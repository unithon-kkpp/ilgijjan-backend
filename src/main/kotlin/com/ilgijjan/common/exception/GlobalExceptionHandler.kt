package com.ilgijjan.common.exception

import io.swagger.v3.oas.annotations.Hidden
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {

	private val log = LoggerFactory.getLogger(this::class.java)

	@ExceptionHandler(Exception::class)
	fun handleServerException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)
        log.error("[InternalServerError] {} {}, message={}", request.method, request.requestURI, e.message, e)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

    @ExceptionHandler(value = [NoResourceFoundException::class, NoHandlerFoundException::class])
    fun handleNotFoundException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND)
        log.warn("[Not Found] {} {}, message={}", request.method, request.requestURI, errorResponse.message)
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED)
        log.warn("[Method Not Allowed] {} {}, message={}", request.method, request.requestURI, errorResponse.message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.bindingResult)
        log.warn("[Validation Failed] {} {}, errors={}", request.method, request.requestURI, errorResponse.errors)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.bindingResult)
        log.warn("[Bind Failed] {} {}, errors={}", request.method, request.requestURI, errorResponse.errors)
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE)
        log.warn("[Type Mismatch] {} {}, parameter={}, message={}", request.method, request.requestURI, e.name, errorResponse.message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
        log.warn("[Message Not Readable] {} {}, message={}", request.method, request.requestURI, errorResponse.message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(MissingServletRequestParameterException::class)
	fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER)
        log.warn("[Missing Parameter] {} {}, parameter={}, message={}", request.method, request.requestURI, e.parameterName, errorResponse.message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

    @ExceptionHandler(value = [HandlerMethodValidationException::class, IllegalArgumentException::class])
    fun handleBadRequestException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
        log.warn("[Bad Request] {} {}, type={}, message={}", request.method, request.requestURI, e::class.simpleName, e.message)
        return ResponseEntity.status(errorResponse.status).body(errorResponse)
    }

	@ExceptionHandler(CustomException::class)
	fun handleCustomException(e: CustomException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(e.errorCode)
        log.warn("[CustomException] {} {}, code={}, message={}", request.method, request.requestURI, errorResponse.code, errorResponse.message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}
}
