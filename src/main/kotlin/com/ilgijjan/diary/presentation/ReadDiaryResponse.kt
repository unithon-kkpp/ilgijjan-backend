package com.ilgijjan.diary.presentation

import com.ilgijjan.diary.domain.Diary
import com.ilgijjan.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter

data class ReadDiaryResponse(
    @field:Schema(description = "일기 ID", example = "123")
    val diaryId: Long,

    @field:Schema(description = "작성 날짜", example = "2025-08-09")
    val date: String,

    @field:Schema(description = "날씨 정보", example = "SUNNY")
    val weather: Weather,

    @field:Schema(description = "감정 정보", example = "5")
    val mood: Int,

    @field:Schema(description = "일기 내용", example = "오늘은 행복한 하루였다.")
    val text: String,

    @field:Schema(description = "일기장 사진 URL", nullable = true, example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @field:Schema(description = "음원 URL", example = "https://example.com/music.mp4")
    val musicUrl: String,

    @field:Schema(description = "가사", example = "...")
    val lyrics: String
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        fun from(diary: Diary): ReadDiaryResponse {
            return ReadDiaryResponse(
                diaryId = diary.id!!,
                date = diary.createdAt?.format(formatter) ?: "0000.00.00",
                weather = diary.weather,
                mood = diary.mood,
                text = diary.text,
                photoUrl = diary.photoUrl,
                imageUrl = diary.imageUrl,
                musicUrl = diary.musicUrl,
                lyrics = diary.lyrics
            )
        }
    }
}
