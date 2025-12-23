package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.music.application.MusicGenerator
import com.ilgijjan.domain.diary.presentation.CreateDiaryRequest
import com.ilgijjan.domain.diary.presentation.CreateDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadDiaryResponse
import com.ilgijjan.domain.diary.presentation.ReadDiariesResponse
import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.integration.text.infrastructure.GeminiTextRefiner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DiaryService(
    private val diaryCreator: DiaryCreator,
    private val diaryReader: DiaryReader,
    private val textExtractor: TextExtractor,
    private val textRefiner: GeminiTextRefiner,
    private val imageGenerator: ImageGenerator,
    private val musicGenerator: MusicGenerator,
    private val userReader: UserReader
) {

    @Transactional
    fun createDiaryWithDummy(userId: Long, request: CreateDiaryRequest): CreateDiaryResponse {
        val user = userReader.getUserById(userId)

        // Dummy
        val command = CreateDiaryCommand.of(
            request,
            user,
            request.text?: "",
            "https://storage.googleapis.com/kkpp-bucket/46763f45-6ed8-4cfa-8d69-bdfd2950278d",
            "https://apiboxfiles.erweima.ai/ZWZjZDg4OTAtNmUwMC00ZjM4LWE5OTQtZjdlYzE3MzgwNWYy.mp3",
            "노래 가사..")

        val diary = diaryCreator.create(command)
        return CreateDiaryResponse(diary.id!!)
    }

    @Transactional
    fun createDiary(userId: Long, request: CreateDiaryRequest): CreateDiaryResponse {
        val user = userReader.getUserById(userId)

        val text = textExtractor.extractText(request.photoUrl, request.text.orEmpty())
        val refinedText = textRefiner.refineText(text)

        val musicFuture = musicGenerator.generateMusicAsync(refinedText)
        val imageFuture = imageGenerator.generateImageAsync(refinedText, request.weather)

        val musicResult = musicFuture.get()
        val imageUrl = imageFuture.get()

        val command = CreateDiaryCommand.of(request, user, text, imageUrl, musicResult.audioUrl, musicResult.lyrics)

        val diary = diaryCreator.create(command)
        return CreateDiaryResponse(diary.id!!)
    }

    fun getDiaryById(diaryId: Long): ReadDiaryResponse {
        val diary = diaryReader.getDiaryById(diaryId)
        return ReadDiaryResponse.from(diary)
    }

    fun findMyDiariesByYearAndMonth(userId: Long, year: Int, month: Int): ReadDiariesResponse {
        val diaries = diaryReader.findAllByUserIdAndDate(userId, year, month)
        return ReadDiariesResponse.from(diaries)
    }
}
