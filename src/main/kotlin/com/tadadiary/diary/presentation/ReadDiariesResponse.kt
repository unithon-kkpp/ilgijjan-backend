package com.tadadiary.diary.presentation

import com.tadadiary.diary.domain.Diary
import java.time.format.DateTimeFormatter

data class ReadDiariesResponse(
    val diaryList: List<DiaryItem>
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
    val id: Long,
    val date: String,
    val imageUrl: String
)