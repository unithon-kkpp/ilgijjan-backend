package com.tadadiary.diary.application

import com.tadadiary.diary.domain.Weather
import com.tadadiary.diary.presentation.CreateDiaryRequest

data class CreateDiaryCommand(
    val text: String,
    val weather: Weather,
    val photoUrl: String?,
    val imageUrl: String,
    val musicUrl: String,
    val lyrics: String,
    val mood: Int
) {
    companion object {
        fun of(
            request: CreateDiaryRequest,
            text: String,
            imageUrl: String,
            musicUrl: String,
            lyrics: String
        ): CreateDiaryCommand {
            return CreateDiaryCommand(
                text = text,
                weather = request.weather,
                photoUrl = request.photoUrl,
                imageUrl = imageUrl,
                musicUrl = musicUrl,
                lyrics = lyrics,
                mood = request.mood
            )
        }
    }
}