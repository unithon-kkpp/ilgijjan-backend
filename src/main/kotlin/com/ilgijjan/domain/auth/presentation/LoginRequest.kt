package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider
import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest (
    @field:Schema(description = "소셜 로그인 제공자", nullable = false, example = "KAKAO")
    val provider: OauthProvider,
    @field:Schema(description = "소셜 서버에서 발급받은 액세스 토큰", nullable = true, example = "...")
    val accessToken: String?
)
