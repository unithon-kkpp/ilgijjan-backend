package com.ilgijjan.diary.presentation

import com.ilgijjan.diary.domain.Diary
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter

data class ReadDiariesResponse(
    @field:Schema(description = "일기 목록")
    val diaryList: List<DiaryItem>
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        fun from(diaries: List<Diary>): ReadDiariesResponse {
            val items = diaries.map { diary ->
                DiaryItem(
                    id = diary.id!!,
                    date = diary.createdAt?.format(formatter) ?: "0000.00.00",
                    imageUrl = diary.imageUrl
                )
            }
            return ReadDiariesResponse(items)
        }
    }
}

data class DiaryItem(
    @field:Schema(description = "일기 ID", example = "123")
    val id: Long,

    @field:Schema(description = "작성 날짜", example = "2025.08.09")
    val date: String,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String
)