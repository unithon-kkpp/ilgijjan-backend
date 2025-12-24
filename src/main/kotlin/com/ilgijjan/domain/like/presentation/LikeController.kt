package com.ilgijjan.domain.like.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.like.application.LikeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/diaries/{diaryId}/likes")
@Tag(name = "Like", description = "좋아요 관련 API입니다.")
class LikeController(
    private val likeService: LikeService
) {

    @PostMapping
    @Operation(summary = "좋아요 등록하기")
    fun addLike(
        @PathVariable diaryId: Long,
        @LoginUser userId: Long
    ): ResponseEntity<Unit> {
        likeService.addLike(diaryId, userId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping
    @Operation(summary = "좋아요 취소하기")
    fun removeLike(
        @PathVariable diaryId: Long,
        @LoginUser userId: Long
    ): ResponseEntity<Unit> {
        likeService.removeLike(diaryId, userId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
