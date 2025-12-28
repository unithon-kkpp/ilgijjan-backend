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
            text = command.text,
            weather = command.weather,
            photoUrl = command.photoUrl,
            imageUrl = command.imageUrl,
            musicUrl = command.musicUrl,
            lyrics = command.lyrics,
            mood = command.mood
        )
        return diaryRepository.save(diary)
    }
}
