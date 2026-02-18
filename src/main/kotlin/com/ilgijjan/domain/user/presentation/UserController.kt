package com.ilgijjan.domain.user.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.user.application.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/me")
@Tag(name = "User", description = "User 관련 API입니다.")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "본인 정보 조회", description = "로그인한 사용자의 이름, 캐릭터, 알림 설정, 보유 음표 개수를 조회합니다.")
    fun getMe(@LoginUser userId: Long): ResponseEntity<ReadMeResponse> {
        val response = userService.getMe(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/notes")
    @Operation(summary = "[개발용] 음표 충전", description = "로그인한 사용자의 음표를 10개(1회 생성 분량) 충전합니다.")
    fun chargeNotes(@LoginUser userId: Long): ResponseEntity<ReadNoteResponse> {
        val response = userService.chargeNotes(userId)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/name")
    @Operation(summary = "이름 변경")
    fun updateName(
        @LoginUser userId: Long,
        @RequestBody @Valid request: UpdateNameRequest
    ): ResponseEntity<Unit> {
        userService.updateName(userId, request.name)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/character")
    @Operation(summary = "캐릭터 변경")
    fun updateCharacter(
        @LoginUser userId: Long,
        @RequestBody @Valid request: UpdateCharacterRequest
    ): ResponseEntity<Unit> {
        userService.updateCharacter(userId, request.character)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/notification")
    @Operation(summary = "알림 수신 여부 설정", description = "true: 켜기, false: 끄기")
    fun updateNotification(
        @LoginUser userId: Long,
        @RequestBody @Valid request: UpdateNotificationRequest
    ): ResponseEntity<Unit> {
        userService.updateNotification(userId, request.isEnabled)
        return ResponseEntity.ok().build()
    }
}
