package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.DiaryStatus
import io.swagger.v3.oas.annotations.media.Schema

data class DiaryStatusResponse(
    @field:Schema(description = "일기 ID", example = "123")
    val diaryId: Long,

    @field:Schema(description = "일기 생성 상태", example = "PENDING")
    val status: DiaryStatus
) {
    companion object {
        fun from(diary: Diary): DiaryStatusResponse {
            return DiaryStatusResponse(
                diaryId = diary.id!!,
                status = diary.status
            )
        }
    }
}
