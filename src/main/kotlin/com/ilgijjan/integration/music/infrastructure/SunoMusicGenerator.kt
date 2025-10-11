package com.ilgijjan.integration.music.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.integration.music.application.MusicResult
import com.ilgijjan.integration.music.application.MusicGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Component
class SunoMusicGenerator(
    private val restTemplate: RestTemplate,
    @Value("\${suno.api.token}")
    private val apiToken: String,
    @Value("\${suno.api.base-url}")
    private val sunoBaseUrl: String,
    @Value("\${api.base-url}")
    private val ourBaseUrl: String
) : MusicGenerator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val taskFutures = ConcurrentHashMap<String, CompletableFuture<MusicResult>>()

    @Async("asyncExecutor")
    override fun generateMusicAsync(text: String): CompletableFuture<MusicResult> {
        log.info("[generateMusicAsync] 비동기 작업 시작 - 스레드: ${Thread.currentThread().name}")
        val result = generateMusic(text)
        log.info("[generateMusicAsync] 비동기 작업 완료 - 스레드: ${Thread.currentThread().name}")
        return CompletableFuture.completedFuture(result)
    }

    // 가사 생성 요청만 하고 taskId 반환
    fun generateMusic(text: String): MusicResult {
        log.info("[generateMusic] 시작 - 입력 텍스트 길이: ${text.length}")
        val bannedWords = listOf("오늘", "같이")
        var sanitizedText = text
        bannedWords.forEach { word ->
            sanitizedText = sanitizedText.replace(word, "")
        }
        val prompt = "Based on the following diary, please write only the Korean children's song lyrics in Korean. Diary: $sanitizedText"
            .take(200)
        val lyricsTaskId = requestLyricsGeneration(prompt)
        log.info("[generateMusic] 가사 생성 요청 완료, taskId: $lyricsTaskId")

        // 결과 기다릴 Future 생성해서 저장
        val future = CompletableFuture<MusicResult>()
        taskFutures[lyricsTaskId] = future

        try {
            return future.get(300, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            taskFutures.remove(lyricsTaskId)
            throw CustomException(ErrorCode.MUSIC_GENERATE_TIMEOUT)
        } catch (ex: Exception) {
            taskFutures.remove(lyricsTaskId)
            throw ex
        }
    }

    fun requestLyricsGeneration(prompt: String): String {
        val url = "$sunoBaseUrl/lyrics"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiToken")
        }
        val requestBody = mapOf(
            "prompt" to prompt,
            "callBackUrl" to "$ourBaseUrl/api/music/lyrics-callback"
        )
        val requestEntity = HttpEntity(requestBody, headers)

        log.info("[requestLyricsGeneration] API 요청 시작: $url")
        val response = restTemplate.postForEntity(url, requestEntity, String::class.java)
        log.info("[requestLyricsGeneration] 응답 상태: ${response.statusCode}")
        log.info("[requestLyricsGeneration] 응답 바디: ${response.body}")

        val mapper = jacksonObjectMapper()
        val taskIdResponse = mapper.readValue(response.body ?: "", TaskIdResponse::class.java)

        if (taskIdResponse.code != 200 || taskIdResponse.data == null) {
            throw RuntimeException("가사 생성 API 오류: ${taskIdResponse.msg}")
        }

        return taskIdResponse.data.taskId
    }

    // 음악 생성 요청
    fun requestMusicGeneration(lyrics: String): String {
        val cleanedLyrics = lyrics.replace(Regex("\\[(.*?)]\\n*"), "")

        val url = "$sunoBaseUrl/generate"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiToken")
        }
        val requestBody = mapOf(
            "prompt" to cleanedLyrics,
            "customMode" to true,
            "style" to "Children",
            "title" to "Generated Song",
            "instrumental" to false,
            "model" to "V4_5",
            "callBackUrl" to "$ourBaseUrl/api/music/music-callback"
        )
        val requestEntity = HttpEntity(requestBody, headers)

        log.info("[requestMusicGeneration] API 요청 시작: $url")
        val response = restTemplate.postForEntity(url, requestEntity, TaskIdResponse::class.java)
        val responseBody = response.body ?: throw RuntimeException("Empty response from Music Generate API")
        if (responseBody.code != 200) throw RuntimeException("Music Generate API error: ${responseBody.msg}")

        return responseBody.data?.taskId ?: throw RuntimeException("Music generation taskId가 없습니다")
    }

    fun transferFutureKey(oldTaskId: String, newTaskId: String) {
        val future = taskFutures.remove(oldTaskId)
        if (future != null) {
            taskFutures[newTaskId] = future
            log.info("Future key transferred from $oldTaskId to $newTaskId")
        } else {
            log.warn("No future found for oldTaskId: $oldTaskId")
        }
    }

    fun onMusicCallback(taskId: String, audioUrl: String, lyrics: String) {
        log.info("[onMusicCallback] 음악 수신, taskId=$taskId")
        val future = taskFutures.remove(taskId)
        if (future != null) {
            future.complete(MusicResult(audioUrl, lyrics))  // 여기서 Future에 결과 전달
        } else {
            log.warn("대기 중인 Future 없음, taskId=$taskId")
        }
    }
}

data class TaskIdResponse(
    val code: Int,
    val msg: String,
    val data: TaskIdData?
)

data class TaskIdData(
    val taskId: String
)

