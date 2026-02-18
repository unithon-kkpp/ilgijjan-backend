package com.ilgijjan.domain.user.presentation

import io.swagger.v3.oas.annotations.media.Schema

data class ReadNoteResponse(
    @field:Schema(description = "음표 개수", example = "10")
    val noteCount: Int
)
