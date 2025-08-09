import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

data class ErrorResponse(
    val time: LocalDateTime = LocalDateTime.now(),
    val status: HttpStatus,
    val message: String,
    val requestURI: String
) {
    companion object {
        fun of(errorCode: ErrorCode, requestURI: String): ErrorResponse {
            return ErrorResponse(
                time = LocalDateTime.now(),
                status = errorCode.status,
                message = errorCode.message,
                requestURI = requestURI
            )
        }
    }
}
