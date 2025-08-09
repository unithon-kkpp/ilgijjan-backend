package com.tadadiary.diary.presentation

import com.tadadiary.diary.domain.Weather

data class CreateDiaryRequest(
    val photoUrl: String?,
    val text: String?,
    val weather: Weather,
    val mood: Int
)