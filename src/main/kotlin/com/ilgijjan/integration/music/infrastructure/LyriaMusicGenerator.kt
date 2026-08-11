package com.ilgijjan.integration.music.infrastructure

import com.ilgijjan.common.exception.NonRetryableException
import com.ilgijjan.integration.music.application.MusicGenerator
import com.ilgijjan.integration.music.application.MusicResult
import com.ilgijjan.integration.storage.application.FileUploader
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.time.Duration
import java.util.Base64
import java.util.concurrent.CompletableFuture

@Profile("!mock")
@Primary
@Component
class LyriaMusicGenerator(
    @Value("\${gemini.api.music-url}")
    private val apiUrl: String,
    @Value("\${gemini.api.music-model}")
    private val model: String,
    @Value("\${gemini.api.key}")
    private val apiKey: String,
    private val fileUploader: FileUploader,
    private val promptBuilder: MusicPromptBuilder,
    restClientBuilder: RestClient.Builder
) : MusicGenerator {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val restClient: RestClient = restClientBuilder.clone()
        .baseUrl(apiUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .requestFactory(
            ClientHttpRequestFactoryBuilder.detect().build(
                ClientHttpRequestFactorySettings.defaults()
                    .withConnectTimeout(Duration.ofSeconds(10))
                    .withReadTimeout(Duration.ofSeconds(120))
            )
        )
        .build()

    @Async("asyncExecutor")
    override fun generateMusicAsync(text: String): CompletableFuture<MusicResult> {
        log.info("[generateMusicAsync] 비동기 작업 시작 - 스레드: ${Thread.currentThread().name}")

        return try {
            val result = generateMusic(text)
            log.info("[generateMusicAsync] 비동기 작업 종료 - 성공, audioUrl: ${result.audioUrl}")
            CompletableFuture.completedFuture(result)
        } catch (e: Exception) {
            log.error("[generateMusicAsync] 작업 실패 - 스레드: ${Thread.currentThread().name}", e)
            CompletableFuture.failedFuture(e)
        }
    }

    fun generateMusic(text: String): MusicResult {
        val prompt = promptBuilder.build(text)
        val requestBody = mapOf(
            "model" to model,
            "input" to prompt
        )

        val response = callLyriaWithRetry(requestBody)
        return extractResult(response)
    }

    private fun callLyriaWithRetry(requestBody: Map<String, Any>): Map<*, *> {
        var lastException: Exception? = null

        for (attempt in 1..5) {
            try {
                log.info("Lyria 요청 시도 ($attempt/5) - model: $model")

                return restClient.post()
                    .body(requestBody)
                    .retrieve()
                    .body(Map::class.java) ?: throw RuntimeException("Lyria 응답 없음")

            } catch (e: HttpClientErrorException) {
                // 4xx는 재시도해도 동일하게 실패하므로 즉시 중단
                throw NonRetryableException("Lyria API 호출 실패 (클라이언트 오류: ${e.statusCode})")
            } catch (e: Exception) {
                lastException = e
                log.warn("Lyria 시도 실패 ($attempt/5). 재시도합니다. 원인: ${e.message}")
            }

            if (attempt < 5) {
                val sleepMs = (1L shl attempt) * 1_000L  // 2s, 4s, 8s, 16s
                log.info("Lyria 재시도 대기 중... ${sleepMs / 1000}초")
                Thread.sleep(sleepMs)
            }
        }
        throw RuntimeException("Lyria API 5회 시도 모두 실패", lastException)
    }

    private fun extractResult(response: Map<*, *>): MusicResult {
        val steps = response["steps"] as? List<*>
            ?: throw RuntimeException("Lyria 응답 포맷 오류 (steps 없음)")

        var audioBase64: String? = null
        // 가사가 verse/chorus 등 여러 text 블록으로 쪼개져 오므로 블록을 섹션 단위로 모은다
        val lyricsSections = mutableListOf<String>()

        steps.filterIsInstance<Map<*, *>>().forEach { step ->
            val content = step["content"] as? List<*> ?: return@forEach
            content.filterIsInstance<Map<*, *>>().forEach { block ->
                when (block["type"]) {
                    "audio" -> if (audioBase64 == null) audioBase64 = block["data"] as? String
                    "text" -> (block["text"] as? String)?.let { lyricsSections.add(it) }
                }
            }
        }

        if (audioBase64 == null) {
            // 안전 필터 차단 등으로 오디오가 없으면 재시도해도 동일하게 막히므로 즉시 실패
            throw NonRetryableException("Lyria 오디오 데이터 없음 (안전 필터 차단 가능)")
        }

        val audioBytes = Base64.getDecoder().decode(audioBase64)
        val gcsUrl = fileUploader.upload(audioBytes, "audio/mpeg")

        // 섹션 내부는 줄바꿈, 섹션 사이는 빈 줄로 문단 구분
        val lyrics = lyricsSections
            .map { cleanLyrics(it) }
            .filter { it.isNotBlank() }
            .joinToString("\n\n")

        return MusicResult(audioUrl = gcsUrl, lyrics = lyrics)
    }

    /** Lyria가 붙이는 타임스탬프/섹션 마커를 제거하고 순수 가사만 남긴다. */
    private fun cleanLyrics(section: String): String {
        return section
            .replace(SECTION_MARKER_REGEX, "")   // [[A0]], [[C2]] 등 섹션 마커
            .replace(TIMESTAMP_REGEX, "")   // [2.4:], [26.4:29.0], [:] 등 타임스탬프
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .trim()
    }

    companion object {
        private val SECTION_MARKER_REGEX = Regex("""\[\[[^]]*]]""")
        private val TIMESTAMP_REGEX = Regex("""\[[\d.]*:?[\d.]*]""")
    }
}
