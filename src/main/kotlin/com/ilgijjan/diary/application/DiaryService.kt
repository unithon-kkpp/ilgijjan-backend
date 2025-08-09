package com.ilgijjan.diary.application

import com.ilgijjan.common.integration.image.ImageGenerator
import com.ilgijjan.common.integration.music.MusicGenerator
import com.ilgijjan.diary.presentation.CreateDiaryRequest
import com.ilgijjan.diary.presentation.CreateDiaryResponse
import com.ilgijjan.diary.presentation.ReadDiaryResponse
import com.ilgijjan.diary.presentation.ReadDiariesResponse
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
        val imageUrl = imageGenerator.generateImage(text, request.weather)
        val (mp4Url, lyrics) = musicGenerator.generateMusic(text)
        val command = CreateDiaryCommand.of(request, text, imageUrl, mp4Url, lyrics)
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