package com.ilgijjan.integration.music.infrastructure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/api/music")
class MusicCallbackController(
    private val sunoMusicGenerator: SunoMusicGenerator,
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    // 가사 생성 콜백 수신
    @PostMapping("/lyrics-callback")
    fun receiveLyricsCallback(@RequestBody rawBody: String): ResponseEntity<String> {
        log.info("원본 바디: $rawBody")
        val mapper = jacksonObjectMapper()
        val callback = mapper.readValue<LyricsCallbackRequest>(rawBody)
        log.info("파싱 결과: $callback")

        if (callback.code == 200) {
            val lyricsData = callback.data.data
            if (lyricsData.isNullOrEmpty()) {
                log.error("[lyrics-callback] 가사 데이터가 없습니다")
                return ResponseEntity.badRequest().body("No lyrics data")
            }

            val firstComplete = lyricsData.firstOrNull { it.status == "complete" }
            if (firstComplete == null) {
                log.error("[lyrics-callback] 완료된 가사 없음")
                return ResponseEntity.badRequest().body("No complete lyrics found")
            }

            val lyrics = firstComplete.text
            log.info("[lyrics-callback] 가사 수신 완료, 길이=${lyrics.length}")

            // 여기서 음악 생성 요청
            val musicTaskId = sunoMusicGenerator.requestMusicGeneration(lyrics)
            log.info("[lyrics-callback] 음악 생성 요청 완료, taskId=$musicTaskId")

            // 여기서 중요한 부분: Future를 음악 taskId로 변경
            sunoMusicGenerator.transferFutureKey(callback.data.taskId ?: "", musicTaskId)

            return ResponseEntity.ok("Lyrics received and music generation started")
        } else {
            log.error("[lyrics-callback] 가사 생성 실패 또는 오류: code=${callback.code}, msg=${callback.msg}")
            // TODO: 실패 처리 로직
            return ResponseEntity.status(500).body("Error in lyrics generation")
        }
    }

    // 음악 생성 콜백 수신
    @PostMapping("/music-callback")
    fun receiveMusicCallback(@RequestBody rawBody: String): ResponseEntity<String> {
        log.info("[music-callback] 원본 바디: $rawBody")

        val mapper = jacksonObjectMapper()
        val callback = mapper.readValue<MusicCallbackRequest>(rawBody)
        log.info("[music-callback] 파싱 결과: $callback")

        if (callback.code == 200) {
            when (callback.data.callbackType) {
                "text" -> {
                    log.info("[music-callback] 중간 텍스트 데이터 수신, 처리 생략")
                    return ResponseEntity.ok("Intermediate text received")
                }
                "complete" -> {
                    val musicDataList = callback.data.data
                    if (musicDataList.isEmpty()) {
                        log.error("[music-callback] 음악 데이터가 없습니다")
                        return ResponseEntity.badRequest().body("No music data")
                    }

                    val firstMusic = musicDataList.first()
                    val audioUrl = listOf(
                        firstMusic.audioUrl,
                        firstMusic.sourceAudioUrl,
                        firstMusic.streamAudioUrl,
                        firstMusic.sourceStreamAudioUrl
                    ).firstOrNull { !it.isNullOrBlank() }

                    if (audioUrl == null) {
                        log.error("[music-callback] 오디오 URL이 없습니다")
                        return ResponseEntity.badRequest().body("No audio URL")
                    }

                    sunoMusicGenerator.onMusicCallback(
                        taskId = callback.data.taskId,
                        audioUrl = audioUrl,
                        lyrics = firstMusic.prompt ?: ""
                    )

                    return ResponseEntity.ok("Music callback processed")
                }
                else -> {
                    log.warn("[music-callback] 알 수 없는 callbackType: ${callback.data.callbackType}")
                    return ResponseEntity.badRequest().body("Unknown callbackType")
                }
            }
        } else {
            log.error("[music-callback] 음악 생성 실패 또는 오류: code=${callback.code}, msg=${callback.msg}")
            return ResponseEntity.status(500).body("Error in music generation")
        }
    }
}

data class LyricsCallbackRequest(
    val code: Int,
    val msg: String,
    val data: LyricsCallbackData
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LyricsCallbackData(
    val callbackType: String,
    @JsonProperty("task_id")
    val taskId: String?,
    val data: List<LyricsVariant>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LyricsVariant(
    val text: String,
    val title: String?,
    val status: String,
    @JsonProperty("error_message")
    val errorMessage: String?
)

data class MusicCallbackRequest(
    val code: Int,
    val msg: String,
    val data: MusicCallbackData
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicCallbackData(
    @JsonProperty("callbackType")
    val callbackType: String,

    @JsonProperty("task_id")
    val taskId: String,

    @JsonProperty("data")
    val data: List<MusicDataItem>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicDataItem(
    @JsonProperty("id")
    val id: String?,

    @JsonProperty("audio_url")
    val audioUrl: String?,

    @JsonProperty("source_audio_url")
    val sourceAudioUrl: String?,

    @JsonProperty("stream_audio_url")
    val streamAudioUrl: String?,

    @JsonProperty("source_stream_audio_url")
    val sourceStreamAudioUrl: String?,

    @JsonProperty("image_url")
    val imageUrl: String?,

    @JsonProperty("source_image_url")
    val sourceImageUrl: String?,

    @JsonProperty("prompt")
    val prompt: String?,

    @JsonProperty("model_name")
    val modelName: String?,

    @JsonProperty("title")
    val title: String?,

    @JsonProperty("tags")
    val tags: String?,

    @JsonProperty("createTime")
    val createTime: String?,

    @JsonProperty("duration")
    val duration: Double?
)

