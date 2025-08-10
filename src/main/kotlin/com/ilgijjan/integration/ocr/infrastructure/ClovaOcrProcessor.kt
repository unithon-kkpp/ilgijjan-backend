package com.ilgijjan.integration.ocr.infrastructure

import com.ilgijjan.integration.ocr.application.OcrProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ClovaOcrProcessor(
    @Value("\${clova-ocr.api.secret}")
    private val secret: String,
    @Value("\${clova-ocr.api.url}")
    private val apiUrl: String
) : OcrProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader("X-OCR-SECRET", secret)
        .build()

    override fun extractText(photoUrl: String): String {
        val requestId = java.util.UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val requestBody = mapOf(
            "version" to "V2",
            "requestId" to requestId,
            "timestamp" to timestamp,
            "lang" to "ko",
            "images" to listOf(
                mapOf(
                    "format" to "jpg",
                    "name" to "demo",
                    "url" to photoUrl
                )
            ),
            "enableTableDetection" to false
        )

        log.info("CLOVA OCR 요청 시작 - requestId: {}, photoUrl: {}", requestId, photoUrl)

        return try {
            val response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OcrResponse::class.java)
                .block() ?: throw RuntimeException("OCR API 응답이 없습니다.")

            log.info("CLOVA OCR 응답 수신 - requestId: {}, inferResult: {}", requestId, response.images?.firstOrNull()?.inferResult)

            val extractedText = response.images
                ?.flatMap { it.fields ?: emptyList() }
                ?.joinToString(" ") { it.inferText ?: "" }
                ?.trim() ?: ""

            log.info("추출된 텍스트 - requestId: {}, text length: {}", requestId, extractedText.length)

            extractedText
        } catch (e: Exception) {
            log.error("CLOVA OCR 호출 실패 - requestId: {}, error: {}", requestId, e.message, e)
            throw e
        }
    }
}

data class OcrResponse(
    val images: List<ImageResult>?
)

data class ImageResult(
    val uid: String?,
    val name: String?,
    val inferResult: String?,
    val message: String?,
    val validationResult: ValidationResult?,
    val convertedImageInfo: ConvertedImageInfo?,
    val fields: List<Field>?
)

data class ValidationResult(
    val result: String?
)

data class ConvertedImageInfo(
    val width: Int?,
    val height: Int?,
    val pageIndex: Int?,
    val longImage: Boolean?
)

data class Field(
    val valueType: String?,
    val inferText: String?,
    val inferConfidence: Double?,
    val type: String?,
    val lineBreak: Boolean?,
    val boundingPoly: BoundingPoly?
)

data class BoundingPoly(
    val vertices: List<Vertex>?
)

data class Vertex(
    val x: Double?,
    val y: Double?
)