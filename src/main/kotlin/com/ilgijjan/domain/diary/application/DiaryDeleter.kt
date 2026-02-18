package com.ilgijjan.domain.diary.application

import org.springframework.stereotype.Component

@Component
class DiaryDeleter(
    private val diaryReader: DiaryReader
) {
    fun delete(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.delete()
    }
}
