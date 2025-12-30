package com.ilgijjan.domain.user.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.user.application.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API입니다.")
class UserController(
    private val userService: UserService
) {
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
}
