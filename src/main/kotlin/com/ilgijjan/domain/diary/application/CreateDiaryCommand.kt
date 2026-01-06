package com.ilgijjan.domain.diary.application

import com.ilgijjan.domain.diary.domain.DiaryInputType
import com.ilgijjan.domain.diary.domain.Weather
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.user.domain.User

data class CreateDiaryCommand(
    val user: User,
    val type: DiaryInputType,
    val text: String?,
    val photoUrl: String?,
    val weather: Weather,
    val mood: Int
) {
    companion object {
        fun of(request: CreateDiaryRequest, user: User): CreateDiaryCommand {
            return CreateDiaryCommand(
                user = user,
                type = request.type,
                text = request.text.takeIf { request.type == DiaryInputType.TEXT },
                photoUrl = request.photoUrl.takeIf { request.type == DiaryInputType.PHOTO },
                weather = request.weather,
                mood = request.mood
            )
        }
    }
}
