package com.ilgijjan.diary.presentation

import com.ilgijjan.diary.application.DiaryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/diaries")
class DiaryController(
    private val diaryService: DiaryService
) {
    @PostMapping
    fun createDiary(@RequestBody @Valid request: CreateDiaryRequest): ResponseEntity<CreateDiaryResponse> {
        val response = diaryService.createDiary(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getDiary(@PathVariable id: Long): ResponseEntity<ReadDiaryResponse> {
        val response = diaryService.getDiaryById(id)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping
    fun findDiaries(): ResponseEntity<ReadDiariesResponse> {
        val response = diaryService.findAll()
        return ResponseEntity(response, HttpStatus.OK)
    }
}