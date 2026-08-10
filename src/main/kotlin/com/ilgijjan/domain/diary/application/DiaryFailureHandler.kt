package com.ilgijjan.domain.diary.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DiaryFailureHandler(
    private val diaryUpdater: DiaryUpdater
) {
    @Transactional
    fun handle(diaryId: Long) {
        diaryUpdater.fail(diaryId)
    }

    @Transactional
    fun handlePermanently(diaryId: Long) {
        diaryUpdater.failPermanently(diaryId)
    }
}
