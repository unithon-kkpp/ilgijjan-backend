package com.ilgijjan.domain.fcmtoken.presentation

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.domain.fcmtoken.application.FcmTokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fcm/tokens")
@Tag(name = "FCM", description = "FCM 관련 API입니다.")
class FcmTokenController(
    private val fcmTokenService: FcmTokenService
) {
    @PostMapping
    @Operation(summary = "FCM Token 등록")
    fun register(@LoginUser userId: Long, @RequestBody request: FcmTokenRequest) {
        fcmTokenService.registerToken(userId, request.token)
    }

    @PatchMapping
    @Operation(summary = "FCM Token 갱신")
    fun renew(@RequestBody request: FcmTokenRequest) {
        fcmTokenService.renewToken(request.token)
    }

    @GetMapping
    @Operation(summary = "[개발용] 본인 FCM Token 목록 조회", description = "FCM 토큰이 정상 등록됐는지 확인하는 용도")
    fun getTokens(@LoginUser userId: Long): List<FcmTokenResponse> {
        return fcmTokenService.getTokens(userId)
    }

    @PostMapping("/test")
    @Operation(summary = "[개발용] FCM 알림 테스트 (본인 기기 발송)")
    fun testNotification(@LoginUser userId: Long) {
        fcmTokenService.sendTestNotification(userId)
    }
}
