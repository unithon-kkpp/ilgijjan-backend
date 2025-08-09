package com.ilgijjan.diary.application

import CustomException
import com.ilgijjan.diary.domain.Diary
import com.ilgijjan.diary.infrastructure.DiaryRepository
import org.springframework.stereotype.Component

@Component
class DiaryReader(
    private val diaryRepository: DiaryRepository
) {
    fun getDiaryById(id: Long): Diary {
        return diaryRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.DIARY_NOT_FOUND) }
    }

    fun findAll(): List<Diary> {
        return diaryRepository.findAll()
    }
}