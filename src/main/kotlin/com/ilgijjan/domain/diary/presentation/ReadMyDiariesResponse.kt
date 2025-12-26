package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter

data class ReadMyDiariesResponse(
    @field:Schema(description = "일기 목록")
    val diaryList: List<MyDiaryItem>
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        fun from(diaries: List<Diary>): ReadMyDiariesResponse {
            val items = diaries.map { diary ->
                MyDiaryItem(
                    id = diary.id!!,
                    date = diary.createdAt?.format(formatter)!!,
                    imageUrl = diary.imageUrl,
                    weather = diary.weather,
                    mood = diary.mood,
                    introLines = diary.lyrics.take(9)
                )
            }
            return ReadMyDiariesResponse(items)
        }
    }
}

data class MyDiaryItem(
    @field:Schema(description = "일기 ID", example = "123")
    val id: Long,

    @field:Schema(description = "작성 날짜", example = "2025.08.09")
    val date: String,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @field:Schema(description = "날씨 정보", example = "SUNNY")
    val weather: Weather,

    @field:Schema(description = "감정 정보", example = "5")
    val mood: Int,

    @field:Schema(description = "가사 첫 두마디", example = "빨간 꽃 노란 꽃")
    val introLines: String
)
