package com.ilgijjan.domain.diary.presentation

import io.swagger.v3.oas.annotations.media.Schema

data class CreateDiaryResponse (
    @field:Schema(description = "생성된 일기 ID", example = "123")
    val diaryId: Long
)