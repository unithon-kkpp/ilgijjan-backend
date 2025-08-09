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

@RestControllerAdvice
class GlobalExceptionHandler {

	private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

	// 500 : Internal Server Error
	@ExceptionHandler(Exception::class)
	fun handleServerException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 404 : Not Found
	@ExceptionHandler(NoResourceFoundException::class)
	protected fun handleNoResourceFoundException(e: NoResourceFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.error(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 405 : Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	protected fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.error(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 400 : MethodArgumentNotValidException
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 400 : MethodArgumentType
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.error(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 400 : Bad Request, ModelAttribute
	@ExceptionHandler(BindException::class)
	fun handleBindException(e: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	@ExceptionHandler(IllegalArgumentException::class)
	fun handleIllegalArgumentException(e: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	@ExceptionHandler(NoHandlerFoundException::class)
	fun handleNoHandlerFoundException(e: NoHandlerFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	@ExceptionHandler(MissingServletRequestParameterException::class)
	fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// 유효성 검사 에러
	@ExceptionHandler(HandlerMethodValidationException::class)
	fun handleHandlerMethodValidationException(e: HandlerMethodValidationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}

	// Custom Exception
	@ExceptionHandler(CustomException::class)
	fun handleCustomException(e: CustomException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
		log.warn(e.message, e)
		val errorResponse = ErrorResponse.of(e.errorCode, request.requestURI)
		return ResponseEntity.status(errorResponse.status.value()).body(errorResponse)
	}
}
