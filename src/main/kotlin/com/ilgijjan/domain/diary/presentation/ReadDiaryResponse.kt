package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.common.utils.DateFormatter
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema

data class  ReadDiaryResponse(
    @field:Schema(description = "일기 ID", example = "123")
    val diaryId: Long,

    @field:Schema(description = "작성 날짜", example = "2025-08-09")
    val date: String,

    @field:Schema(description = "날씨 정보", example = "SUNNY")
    val weather: Weather,

    @field:Schema(description = "감정 정보", example = "5")
    val mood: Int,

    @field:Schema(description = "일기장 사진 URL", nullable = true, example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @field:Schema(description = "음원 URL", example = "https://example.com/music.mp4")
    val musicUrl: String,

    @field:Schema(description = "가사", example = "...")
    val lyrics: String,

    @field:Schema(description = "공개 여부", example = "true")
    val isPublic: Boolean,

    @field:Schema(description = "좋아요 수", example = "10")
    val likeCount: Long
) {
    companion object {
        fun from(diary: Diary, isOwner: Boolean): ReadDiaryResponse {
            return ReadDiaryResponse(
                diaryId = diary.id!!,
                date = diary.createdAt?.format(DateFormatter.DOT_DATE_FORMATTER)!!,
                weather = diary.weather,
                mood = diary.mood,
                photoUrl = if (isOwner) diary.photoUrl else null,
                imageUrl = diary.imageUrl,
                musicUrl  = diary.musicUrl,
                lyrics = diary.lyrics,
                isPublic = diary.isPublic,
                likeCount = diary.likeCount
            )
        }
    }
}
