package com.ilgijjan.diary.presentation

import com.ilgijjan.diary.domain.Weather

data class CreateDiaryRequest(
    val photoUrl: String?,
    val text: String?,
    val weather: Weather,
    val mood: Int
)