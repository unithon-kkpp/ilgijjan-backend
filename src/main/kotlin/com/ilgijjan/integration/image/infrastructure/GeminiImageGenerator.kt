package com.ilgijjan.integration.image.infrastructure

import com.ilgijjan.domain.diary.domain.Weather
import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.storage.application.FileUploader
import io.netty.channel.ChannelOption
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.Base64
import java.util.concurrent.CompletableFuture

@Primary
@Component
class GeminiImageGenerator(
    @Value("\${gemini.api.image-url}")
    private val apiUrl: String,
    @Value("\${gemini.api.key}")
    private val apiKey: String,
    private val fileUploader: FileUploader
) : ImageGenerator {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(apiUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
        }
        .clientConnector(ReactorClientHttpConnector(
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .responseTimeout(Duration.ofSeconds(60))
        ))
        .build()

    @Async("asyncExecutor")
    override fun generateImageAsync(text: String, weather: Weather): CompletableFuture<String> {
        log.info("[generateImageAsync] 비동기 작업 시작 - 스레드: ${Thread.currentThread().name}")

        return try {
            val imageUrl = generateImage(text, weather)
            log.info("[generateImageAsync] 비동기 작업 종료 - 성공")
            CompletableFuture.completedFuture(imageUrl)
        } catch (e: Exception) {
            log.error("[generateImageAsync] 작업 실패 - 스레드: ${Thread.currentThread().name}", e)
            CompletableFuture.failedFuture(e)
        }
    }

    fun generateImage(text: String, weather: Weather): String {
        val prompt = buildPrompt(text, weather)

        val safetySettings = listOf(
            mapOf("category" to "HARM_CATEGORY_HARASSMENT", "threshold" to "BLOCK_ONLY_HIGH"),
            mapOf("category" to "HARM_CATEGORY_HATE_SPEECH", "threshold" to "BLOCK_ONLY_HIGH"),
            mapOf("category" to "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold" to "BLOCK_ONLY_HIGH"),
            mapOf("category" to "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold" to "BLOCK_ONLY_HIGH")
        )

        val requestBody = mapOf(
            "contents" to listOf(
                mapOf(
                    "parts" to listOf(
                        mapOf("text" to prompt)
                    )
                )
            ),
            "safetySettings" to safetySettings
        )

        var lastException: Exception? = null

        for (attempt in 1..2) {
            try {
                log.info("Gemini 요청 시도 ($attempt/2)")

                val response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map::class.java)
                    .block() ?: throw RuntimeException("응답 없음")

                return extractAndUploadImage(response)

            } catch (e: Exception) {
                lastException = e
                log.warn("Gemini 1차 시도 실패. 재시도합니다. 원인: ${e.message}")

                if (attempt == 1) {
                    Thread.sleep(1000)
                }
            }
        }
        throw RuntimeException("Gemini API 2회 시도 모두 실패", lastException)
    }

    private fun buildPrompt(text: String, weather: Weather): String {
        val weatherStr = weather.name.lowercase()
        return """
            Cute, colorful children's storybook illustration based on this story: "$text". 
            Show "$weatherStr" weather in background. 
            Square (1:1) aspect ratio.
            Do not include text in the image.
        """.trimIndent()
    }

    private fun extractAndUploadImage(response: Map<*, *>): String {
        val candidates = response["candidates"] as? List<*>
            ?: throw RuntimeException("응답 포맷 오류 (candidates 없음)")

        val candidate = candidates.firstOrNull() as? Map<*, *>

        if (candidate?.get("finishReason") != "STOP") {
            throw RuntimeException("이미지 생성 거부됨 (사유: ${candidate?.get("finishReason")})")
        }

        val base64Data = (((candidate["content"] as? Map<*, *>)
            ?.get("parts") as? List<*>)
            ?.firstOrNull() as? Map<*, *>)
            ?.get("inlineData")?.let { (it as Map<*, *>)["data"] as? String }
            ?: throw RuntimeException("이미지 Base64 데이터 없음")

        return fileUploader.upload(Base64.getDecoder().decode(base64Data))
    }
}
