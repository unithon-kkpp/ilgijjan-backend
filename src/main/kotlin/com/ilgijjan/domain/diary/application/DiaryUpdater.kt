package com.ilgijjan.domain.diary.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    fun updateResult(diaryId: Long, command: UpdateDiaryResultCommand) {
        val diary = diaryReader.getDiaryById(diaryId)

        diary.text = command.refinedText
        diary.imageUrl = command.imageUrl
        diary.musicUrl = command.musicUrl
        diary.lyrics = command.lyrics

        diary.complete()
    }

    @Transactional
    fun fail(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.fail()
    }
}
