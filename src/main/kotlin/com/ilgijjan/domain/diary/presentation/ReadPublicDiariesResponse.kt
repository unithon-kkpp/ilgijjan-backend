package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.common.utils.DateFormatter
import com.ilgijjan.domain.diary.domain.Diary
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Slice

data class ReadPublicDiariesResponse(
    @field:Schema(description = "일기 목록")
    val diaryList: List<PublicDiaryItem>,

    @field:Schema(description = "마지막 일기 ID (다음 요청 시 사용)", example = "122")
    val lastId: Long?,

    @field:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean
) {
    companion object {
        fun from(slice: Slice<Diary>): ReadPublicDiariesResponse {
            val items = slice.content.map { diary ->
                PublicDiaryItem(
                    id = diary.id!!,
                    date = diary.createdAt?.format(DateFormatter.DOT_DATE_FORMATTER)!!,
                    imageUrl = diary.imageUrl ?: "",
                    authorName = diary.user.getMaskedName(),
                    introLines = diary.lyrics?.substringBefore("\n")?.take(9)
                )
            }

            return ReadPublicDiariesResponse(
                diaryList = items,
                lastId = items.lastOrNull()?.id,
                hasNext = slice.hasNext()
            )
        }
    }
}

data class PublicDiaryItem(
    @field:Schema(description = "일기 ID", example = "123")
    val id: Long,

    @field:Schema(description = "작성 날짜", example = "2025.08.09")
    val date: String,

    @field:Schema(description = "생성된 이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @field:Schema(description = "작성자 이름", example = "김철수")
    val authorName: String,

    @field:Schema(description = "가사 첫 두마디", example = "빨간 꽃 노란 꽃")
    val introLines: String?
)
