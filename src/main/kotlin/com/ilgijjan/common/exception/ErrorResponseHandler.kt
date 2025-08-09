import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.LinkedHashMap

@Component
class ErrorResponseHandler {

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun sendErrorResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        errorCode: ErrorCode
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = errorCode.status.value()

        val errorResponse = LinkedHashMap<String, Any>().apply {
            put("time", LocalDateTime.now())
            put("status", errorCode.status)
            put("message", errorCode.message)
            put("requestURI", request.requestURI)
        }

        response.writer.use { writer ->
            objectMapper.writeValue(writer, errorResponse)
        }
    }
}
