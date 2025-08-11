package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.domain.diary.application.DiaryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/diaries")
@Tag(name = "Diary", description = "Diary 관련 API입니다.")
class DiaryController(
    private val diaryService: DiaryService
) {
    @PostMapping("/test")
    @Operation(summary = "일기 작성하기 테스트")
    fun createDiaryWithDummy(@RequestBody @Valid request: CreateDiaryRequest): ResponseEntity<CreateDiaryResponse> {
        val response = diaryService.createDiaryWithDummy(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping
    @Operation(summary = "일기 작성하기")
    fun createDiary(@RequestBody @Valid request: CreateDiaryRequest): ResponseEntity<CreateDiaryResponse> {
        val response = diaryService.createDiary(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    @Operation(summary = "일기 단건 조회")
    fun getDiary(@PathVariable id: Long): ResponseEntity<ReadDiaryResponse> {
        val response = diaryService.getDiaryById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    @Operation(summary = "일기 목록 조회")
    fun findDiaries(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ReadDiariesResponse> {
        val response = diaryService.findAllByYearAndMonth(year, month)
        return ResponseEntity(response, HttpStatus.OK)
    }
}