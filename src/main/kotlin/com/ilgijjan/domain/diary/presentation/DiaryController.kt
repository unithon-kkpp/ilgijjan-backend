package com.ilgijjan.domain.diary.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.diary.application.DiaryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
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
    fun getDiary(@PathVariable diaryId: Long,
                 @LoginUser userId: Long
    ): ResponseEntity<ReadDiaryResponse> {
        val response = diaryService.getDiaryById(diaryId, userId)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "본인 일기 목록 조회",
        description = "연도와 월을 기준으로 사용자 본인이 작성한 일기 목록을 조회합니다."
    )
    fun getMyDiaries(
        @LoginUser userId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ReadMyDiariesResponse> {
        val response = diaryService.getMyDiariesByYearAndMonth(userId, year, month)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/public")
    @Operation(
        summary = "공개 일기 목록 조회",
        description = """
            공개 설정된 일기들을 최신순으로 조회합니다. 무한 스크롤 방식을 지원합니다.
            
            1. 첫 요청 시: lastId를 비우고 요청합니다. (최신글부터 조회)
            2. 추가 스크롤 시: 이전 응답에서 받은 'lastId' 값을 쿼리 파라미터에 넣어서 요청합니다.
            3. hasNext가 false가 될 때까지 반복 호출 가능합니다.
        """
    )
    fun getPublicDiaries(
        @RequestParam(required = false) lastId: Long?,
        @RequestParam(defaultValue = "20") @Max(100) size: Int
    ): ResponseEntity<ReadPublicDiariesResponse> {
        val response = diaryService.getPublicDiaries(lastId, size)
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

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "일기 삭제")
    fun deleteDiary(
        @PathVariable diaryId: Long,
        @LoginUser userId: Long
    ): ResponseEntity<Unit> {
        diaryService.deleteDiary(diaryId)
        return ResponseEntity.noContent().build()
    }
}
