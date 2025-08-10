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
    fun getDiaryById(id: Long): Diary {
        return diaryRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.DIARY_NOT_FOUND) }
    }

    fun findAllByYearAndMonth(year: Int, month: Int): List<Diary> {
        return diaryRepository.findAllByYearAndMonth(year, month)
    }
}