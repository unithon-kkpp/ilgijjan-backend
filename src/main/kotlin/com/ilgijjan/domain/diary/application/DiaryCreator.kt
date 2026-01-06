package com.ilgijjan.domain.diary.application

import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.diary.infrastructure.DiaryRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DiaryCreator (
    private val diaryRepository: DiaryRepository
){
    @Transactional
    fun create(command: CreateDiaryCommand): Diary {
        val diary = Diary(
            user = command.user,
            type = command.type,
            text = command.text,
            photoUrl = command.photoUrl,
            weather = command.weather,
            mood = command.mood
        )
        return diaryRepository.save(diary)
    }
}
