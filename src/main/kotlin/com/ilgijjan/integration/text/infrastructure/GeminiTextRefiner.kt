package com.ilgijjan.integration.text.infrastructure

import com.ilgijjan.integration.text.application.TextRefiner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GeminiTextRefiner(
    @Value("\${gemini.api.url}")
    private val apiUrl: String,
    @Value("\${gemini.api.key}")
    private val apiKey: String
): TextRefiner {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val webClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .build()

    override fun refineText(text: String): String {
        val prompt = """
        $text
        Please refine this diary entry into concise English within 100 characters, capturing the core meaning.
        Respond **only with the refined text**. Do NOT include any explanations, greetings, or additional comments.
        """.trimIndent()

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
        log.info("Gemini API 요청 바디: {}", requestBody)

        val response = webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(GeminiEditResponse::class.java)
            .block() ?: throw RuntimeException("Gemini API 응답이 없습니다.")

        log.info("Gemini API 원본 응답 JSON: {}", response)

        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?.trim()
            ?: ""
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