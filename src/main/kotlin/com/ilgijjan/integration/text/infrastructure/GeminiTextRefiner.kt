package com.ilgijjan.integration.text.infrastructure

import com.ilgijjan.common.exception.NonRetryableException
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

        var lastException: Exception? = null

        for (attempt in 1..5) {
            try {
                log.info("Gemini 텍스트 정제 시도 ($attempt/5)")

                val response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GeminiEditResponse::class.java)
                    .block() ?: throw RuntimeException("Gemini API 응답 바디가 비어있습니다.")

                val refinedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""

                log.info("<<< [Gemini-RES] result={}", refinedText)

                return refinedText
            } catch (e: NonRetryableException) {
                throw e
            } catch (e: Exception) {
                lastException = e
                log.warn("Gemini 텍스트 정제 시도 실패 ($attempt/5). 재시도합니다. 원인: ${e.message}")

                if (attempt < 5) {
                    val sleepMs = (1L shl attempt) * 1_000L
                    Thread.sleep(sleepMs)
                }
            }
        }
        throw RuntimeException("Gemini 텍스트 정제 5회 시도 모두 실패", lastException)
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