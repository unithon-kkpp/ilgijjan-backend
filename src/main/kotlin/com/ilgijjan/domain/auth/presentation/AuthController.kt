package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.auth.application.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API입니다.")
class AuthController (
    private val authService: AuthService
){

    @PostMapping("/login")
    @Operation(summary = "로그인")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(
        @LoginUser userId: Long,
        @RequestHeader("Refresh-Token") refreshToken: String,
        @RequestBody @Valid request: LogoutRequest): ResponseEntity<Unit> {
        authService.logout(userId, refreshToken, request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    fun withdraw(
        @LoginUser userId: Long,
        @RequestHeader("Refresh-Token") refreshToken: String,
        @RequestBody @Valid request: WithdrawRequest
    ): ResponseEntity<Unit> {
        authService.withdraw(userId, refreshToken, request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 Access/Refresh 토큰을 재발급합니다.")
    fun reissue(
        @RequestHeader("Refresh-Token") refreshToken: String
    ): ResponseEntity<ReissueResponse> {
        val response = authService.reissue(refreshToken)
        return ResponseEntity.ok(response)
    }
}
