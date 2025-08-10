package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CreateDiaryRequest(
    @field:Schema(description = "일기장 사진 URL", nullable = true, example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @field:Schema(description = "일기 내용", nullable = true, example = "오늘은 좋은 하루였다.")
    val text: String?,

    @field:Schema(description = "날씨 정보", nullable = false, example = "SUNNY")
    val weather: Weather,

    @field:Min(1)
    @field:Max(9)
    @field:Schema(description = "감정 정보", nullable = false, example = "5")
    val mood: Int
)