package com.ilgijjan.common.exception

import io.swagger.v3.oas.annotations.Hidden
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
	fun handleServerException(e: Exception): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(NoResourceFoundException::class)
	fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ErrorResponse> {
		val errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
		log.error(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors.joinToString(", ") { "[${it.field}] ${it.defaultMessage}" }
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, message)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
		log.error(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(BindException::class)
	fun handleBindException(e: BindException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(IllegalArgumentException::class)
	fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(NoHandlerFoundException::class)
	fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(MissingServletRequestParameterException::class)
	fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(HandlerMethodValidationException::class)
	fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}

	@ExceptionHandler(CustomException::class)
	fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
        log.warn("CustomException 발생: {} - {}", e.errorCode.status, e.errorCode.message)
		val errorResponse = ErrorResponse.of(e.errorCode)
		return ResponseEntity.status(errorResponse.status).body(errorResponse)
	}
}
