package com.ilgijjan.diary.presentation

import com.ilgijjan.diary.domain.Diary
import com.ilgijjan.diary.domain.Weather
import java.time.format.DateTimeFormatter

data class ReadDiaryResponse(
    val diaryId: Long,
    val date: String,
    val weather: Weather,
    val mood: Int,
    val text: String,
    val photoUrl: String?,
    val imageUrl: String,
    val musicUrl: String,
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
