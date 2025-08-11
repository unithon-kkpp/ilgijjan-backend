package com.ilgijjan.domain.diary.application

import com.ilgijjan.integration.image.application.ImageGenerator
import com.ilgijjan.integration.music.application.MusicGenerator
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
    fun createDiaryWithDummy(request: CreateDiaryRequest): CreateDiaryResponse {
        // Dummy
        val command = CreateDiaryCommand.of(
            request,
            request.text?: "",
            "https://storage.googleapis.com/kkpp-bucket/46763f45-6ed8-4cfa-8d69-bdfd2950278d",
            "https://apiboxfiles.erweima.ai/ZWZjZDg4OTAtNmUwMC00ZjM4LWE5OTQtZjdlYzE3MzgwNWYy.mp3",
            "노래 가사..")

        val diary = diaryCreator.create(command)
        return CreateDiaryResponse(diary.id!!)
    }

    fun getDiaryById(id: Long): ReadDiaryResponse {
        val diary = diaryReader.getDiaryById(id)
        return ReadDiaryResponse.from(diary)
    }

    fun findAllByYearAndMonth(year: Int, month: Int): ReadDiariesResponse {
        val diaries = diaryReader.findAllByYearAndMonth(year, month)
        return ReadDiariesResponse.from(diaries)
    }
}