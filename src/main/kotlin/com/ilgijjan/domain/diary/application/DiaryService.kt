package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.image.ImageGenerator
import com.ilgijjan.integration.music.MusicGenerator
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.diary.presentation.CreateDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadDiariesResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DiaryService(
    private val diaryCreator: DiaryCreator,
    private val diaryReader: DiaryReader,
    private val textExtractor: TextExtractor,
    private val imageGenerator: ImageGenerator,
    private val musicGenerator: MusicGenerator,
) {

    @Transactional
    fun createDiary(request: CreateDiaryRequest): CreateDiaryResponse {
        val text = textExtractor.extractText(request.photoUrl, request.text.orEmpty())
        // 비동기 처리 필요 : 이미지 생성 / 음원 생성
        val imageUrl = imageGenerator.generateImage(text, request.weather)
        val musicResult = musicGenerator.generateMusic(text)
        val command = CreateDiaryCommand.of(request, text, imageUrl, musicResult.audioUrl, musicResult.lyrics)
        val diary = diaryCreator.create(command)
        return CreateDiaryResponse(diary.id!!)
    }

    fun getDiaryById(id: Long): ReadDiaryResponse {
        val diary = diaryReader.getDiaryById(id)
        return ReadDiaryResponse.from(diary)
    }

    fun findAll(): ReadDiariesResponse {
        val diaries = diaryReader.findAll()
        return ReadDiariesResponse.from(diaries)
    }
}