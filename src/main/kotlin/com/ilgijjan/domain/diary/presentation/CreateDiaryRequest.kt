package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.domain.DiaryInputType
import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@ValidDiaryRequest
data class CreateDiaryRequest(

    @field:Schema(description = "일기 생성 방식 (TEXT 또는 PHOTO)", nullable = false, example = "TEXT")
    val type: DiaryInputType,

    @field:Schema(description = "직접 입력한 일기 내용 (type이 TEXT일 때 필수)", nullable = false, example = "오늘 날씨가 너무 좋았다.")
    val text: String?,

    @field:Schema(description = "스캔할 일기 사진 URL (type이 PHOTO일 때 필수)", nullable = false, example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @field:Schema(description = "날씨 정보", nullable = false, example = "SUNNY")
    val weather: Weather,

    @field:Min(1)
    @field:Max(9)
    @field:Schema(description = "감정 정보 (1~9)", nullable = false, example = "5")
    val mood: Int
)
