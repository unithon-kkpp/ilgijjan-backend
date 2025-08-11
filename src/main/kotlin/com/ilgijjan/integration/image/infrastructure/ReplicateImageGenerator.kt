package com.ilgijjan.integration.image.infrastructure

import com.ilgijjan.domain.diary.domain.Weather
import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.music.application.MusicResult
import com.ilgijjan.integration.text.infrastructure.GeminiTextRefiner
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.CompletableFuture

@Component
class ReplicateImageGenerator(
    @Value("\${replicate.api.base-url}") private val baseUrl: String,
    @Value("\${replicate.api.token}") private val apiToken: String,
    @Value("\${replicate.api.model-version}") private val modelVersion: String,
) : ImageGenerator {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val webClient: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    @Async("asyncExecutor")
    override fun generateImageAsync(text: String, weather: Weather): CompletableFuture<String> {
        log.info("[generateImageAsync] 비동기 작업 시작 - 스레드: ${Thread.currentThread().name}")
        val imageUrl = generateImage(text, weather)
        log.info("[generateImageAsync] 비동기 작업 종료 - 스레드: ${Thread.currentThread().name}")
        return CompletableFuture.completedFuture(imageUrl)
    }

    fun generateImage(text: String, weather: Weather): String {
        val prompt = buildPrompt(text, weather)

        val requestBody = mapOf(
            "version" to modelVersion,
            "input" to mapOf(
                "prompt" to prompt
            )
        )
        log.info("Replicate API에 이미지 생성 요청 보냄 - 프롬프트: {}", prompt)

        val initialResponse = try {
            webClient.post()
                .header("Authorization", "Bearer $apiToken")
                .header("Content-Type", "application/json")
                .header("Prefer", "wait")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map::class.java)
                .block() ?: throw RuntimeException("Replicate API 응답 없음")
        } catch (e: Exception) {
            log.error("Replicate API 호출 실패", e)
            throw e
        }
        log.info("초기 응답 전체: {}", initialResponse)
        val status = initialResponse["status"] as? String ?: throw RuntimeException("응답에 status가 없음")
        val id = initialResponse["id"] as? String ?: throw RuntimeException("응답에 id가 없음")

        when (status) {
            "starting", "processing" -> {
                log.info("작업이 아직 진행 중입니다. 폴링을 시작합니다. id={}", id)
                while (true) {
                    val statusResponse = try {
                        webClient.get()
                            .uri("/$id")
                            .header("Authorization", "Bearer $apiToken")
                            .retrieve()
                            .bodyToMono(Map::class.java)
                            .block() ?: throw RuntimeException("상태 조회 응답 없음")
                    } catch (e: Exception) {
                        log.error("Replicate 상태 조회 실패", e)
                        throw e
                    }

                    log.info("폴링 상태 응답 전체: {}", statusResponse)

                    val pollStatus = statusResponse["status"] as? String ?: "unknown"
                    log.info("폴링 중 상태: {}", pollStatus)

                    when (pollStatus) {
                        "succeeded" -> {
                            val output = extractImageUrl(statusResponse["output"])
                            return output ?: throw RuntimeException("응답에 이미지 URL이 없음")
                        }

                        "failed", "canceled" -> throw RuntimeException("이미지 생성 실패 또는 취소됨")
                        "starting", "processing" -> Thread.sleep(1000)
                        else -> throw RuntimeException("알 수 없는 상태: $pollStatus")
                    }
                }
            }

            "succeeded" -> {
                val output = extractImageUrl(initialResponse["output"])
                return output ?: throw RuntimeException("응답에 이미지 URL이 없음")
            }

            "failed", "canceled" -> throw RuntimeException("이미지 생성 실패 또는 취소됨")
            else -> throw RuntimeException("알 수 없는 상태: $status")
        }
    }

    // 77자 이내로 압축 필요 (specified maximum sequence length for this model : 77)
    private fun buildPrompt(text: String, weather: Weather): String {
        val weatherStr = weather.name.lowercase()
        return "Cute, colorful children's storybook illustration from: \"$text\". Show \"$weatherStr\" weather in background. Square (1:1) aspect ratio."
    }

    private fun extractImageUrl(output: Any?): String? {
        return when (output) {
            is String -> output
            is List<*> -> output.firstOrNull() as? String
            else -> null
        }
    }
}