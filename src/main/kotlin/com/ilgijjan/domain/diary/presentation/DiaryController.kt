package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.common.annotation.LoginUser
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
    fun createDiaryWithDummy(
        @LoginUser userId: Long,
        @RequestBody @Valid request: CreateDiaryRequest
    ): ResponseEntity<CreateDiaryResponse> {
        val response = diaryService.createDiaryWithDummy(userId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping
    @Operation(summary = "일기 작성하기")
    fun createDiary(
        @LoginUser userId: Long,
        @RequestBody @Valid request: CreateDiaryRequest
    ): ResponseEntity<CreateDiaryResponse> {
        val response = diaryService.createDiary(userId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/{diaryId}")
    @Operation(summary = "일기 단건 조회")
    fun getDiary(@PathVariable diaryId: Long): ResponseEntity<ReadDiaryResponse> {
        val response = diaryService.getDiaryById(diaryId)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    @Operation(summary = "내 일기 목록 조회")
    fun findMyDiaries(
        @LoginUser userId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ReadDiariesResponse> {
        val response = diaryService.findMyDiariesByYearAndMonth(userId, year, month)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PatchMapping("/{diaryId}/publish")
    @Operation(summary = "일기 공개 설정")
    fun publishDiary(
        @PathVariable diaryId: Long,
        @LoginUser userId: Long 
    ): ResponseEntity<Unit> {
        diaryService.publishDiary(diaryId)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{diaryId}/unpublish")
    @Operation(summary = "일기 비공개 설정")
    fun unpublishDiary(
        @PathVariable diaryId: Long,
        @LoginUser userId: Long
    ): ResponseEntity<Unit> {
        diaryService.unpublishDiary(diaryId)
        return ResponseEntity.ok().build()
    }
}
