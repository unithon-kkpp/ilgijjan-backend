package com.ilgijjan.domain.diary.application

import org.springframework.stereotype.Component

@Component
class DiaryUpdater(
    private val diaryReader: DiaryReader
) {
    fun publish(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.publish()
    }

    fun unpublish(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.unpublish()
    }
}
