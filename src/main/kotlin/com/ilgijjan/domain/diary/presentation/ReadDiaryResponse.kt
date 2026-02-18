package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.common.utils.DateFormatter
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.domain.DiaryInputType
import com.ilgijjan.domain.diary.domain.Weather
import io.swagger.v3.oas.annotations.media.Schema

data class ReadDiaryResponse(
    @field:Schema(description = "일기 ID", example = "123")
    val diaryId: Long,

    @field:Schema(description = "일기 생성 방식 (TEXT 또는 PHOTO)", nullable = false, example = "TEXT")
    val type: DiaryInputType,

    @field:Schema(description = "일기 주인 여부", example = "true")
    val isOwner: Boolean,

    @field:Schema(description = "직접 입력한 일기 내용 (주인일 때만 반환)", nullable = true, example = "오늘 날씨가 너무 좋았다.")
    val text: String?,

    @field:Schema(description = "일기장 사진 URL (주인일 때만 반환)", nullable = true, example = "https://example.com/photo.jpg")
    val photoUrl: String?,

    @field:Schema(description = "작성 날짜", example = "2025-08-09")
    val date: String,

    @field:Schema(description = "날씨 정보", example = "SUNNY")
    val weather: Weather,

    @field:Schema(description = "감정 정보", example = "5")
    val mood: Int,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String?,

    @field:Schema(description = "음원 URL", example = "https://example.com/music.mp4")
    val musicUrl: String?,

    @field:Schema(description = "가사", example = "...")
    val lyrics: String?,

    @field:Schema(description = "공개 여부", example = "true")
    val isPublic: Boolean,

    @field:Schema(description = "좋아요 수", example = "10")
    val likeCount: Long,

    @field:Schema(description = "좋아요 여부", example = "false")
    val isLiked: Boolean
) {
    companion object {
        fun from(diary: Diary, isOwner: Boolean, isLiked: Boolean): ReadDiaryResponse {
            return ReadDiaryResponse(
                diaryId = diary.id!!,
                type = diary.type,
                isOwner = isOwner,
                text = if (isOwner) diary.text else null,
                photoUrl = if (isOwner) diary.photoUrl else null,
                date = diary.createdAt?.format(DateFormatter.DOT_DATE_FORMATTER)!!,
                weather = diary.weather,
                mood = diary.mood,
                imageUrl = diary.imageUrl,
                musicUrl = diary.musicUrl,
                lyrics = diary.lyrics,
                isPublic = diary.isPublic,
                likeCount = diary.likeCount,
                isLiked = isLiked
            )
        }
    }
}
