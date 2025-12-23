package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.infrastructure.DiaryRepository
import org.springframework.stereotype.Component

@Component
class DiaryReader(
    private val diaryRepository: DiaryRepository
) {
    fun getDiaryById(diaryId: Long): Diary {
        return diaryRepository.findById(diaryId)
            .orElseThrow { CustomException(ErrorCode.DIARY_NOT_FOUND) }
    }

    fun findAllByUserIdAndDate(userId: Long, year: Int, month: Int): List<Diary> {
        return diaryRepository.findAllByUserIdAndYearAndMonth(userId, year, month)
    }
}
