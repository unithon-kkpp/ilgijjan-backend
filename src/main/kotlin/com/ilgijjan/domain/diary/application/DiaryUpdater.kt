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
    fun complete(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.complete()
    }

    @Transactional
    fun fail(diaryId: Long) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.fail()
    }

    @Transactional
    fun saveExtractedText(diaryId: Long, extractedText: String) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.saveExtractedText(extractedText)
    }

    @Transactional
    fun saveRefinedText(diaryId: Long, refinedText: String) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.saveRefinedText(refinedText)
    }

    @Transactional
    fun saveImage(diaryId: Long, imageUrl: String) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.saveImage(imageUrl)
    }

    @Transactional
    fun saveMusic(diaryId: Long, musicUrl: String, lyrics: String) {
        val diary = diaryReader.getDiaryById(diaryId)
        diary.saveMusic(musicUrl, lyrics)
    }
}
