package com.ilgijjan.domain.diary.application

import com.ilgijjan.domain.diary.domain.Weather
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.user.domain.User

data class CreateDiaryCommand(
    val user: User,
    val text: String,
    val weather: Weather,
    val photoUrl: String,
    val imageUrl: String,
    val musicUrl: String,
    val lyrics: String,
    val mood: Int
) {
    companion object {
        fun of(request: CreateDiaryRequest, user: User): CreateDiaryCommand {
            return CreateDiaryCommand(
                user = user,
                text = "",
                weather = request.weather,
                photoUrl = request.photoUrl,
                imageUrl = "", musicUrl = "", lyrics = "",
                mood = request.mood
            )
        }
    }
}
