package com.ilgijjan.integration.text.infrastructure

import com.ilgijjan.integration.text.application.TextRefiner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GeminiTextRefiner(
    @Value("\${gemini.api.text-url}")
    private val apiUrl: String,
    @Value("\${gemini.api.key}")
    private val apiKey: String,
    webClientBuilder: WebClient.Builder
): TextRefiner {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val webClient = webClientBuilder
        .baseUrl(apiUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .build()

    override fun refineText(text: String): String {
        log.info(">>> [Gemini-REQ] originalText={}", text)
        val prompt = "$text Please refine this diary entry into concise English within 80 characters, capturing the core meaning. Output only the refined text without any extra explanations."

        val requestBody = mapOf(
            "contents" to listOf(
                mapOf(
                    "parts" to listOf(
                        mapOf(
                            "text" to prompt
                        )
                    )
                )
            )
        )

        return try {
            val response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiEditResponse::class.java)
                .block() ?: throw RuntimeException("Gemini API 응답 바디가 비어있습니다.")

            val refinedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""

            log.info("<<< [Gemini-RES] result={}", refinedText)

            refinedText
        } catch (e: Exception) {
            log.error("[Gemini-ERR] message={}", e.message)
            throw e
        }
    }
}

data class GeminiEditResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)

data class Content(
    val parts: List<Part>?
)

data class Part(
    val text: String?
)