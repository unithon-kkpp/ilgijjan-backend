package com.ilgijjan.domain.auth.presentation

import com.ilgijjan.domain.auth.domain.OauthProvider
import io.swagger.v3.oas.annotations.media.Schema

interface OauthRequest {
    @get:Schema(description = "소셜 로그인 제공자", nullable = false, example = "KAKAO")
    val provider: OauthProvider
    @get:Schema(description = "소셜 액세스 토큰", nullable = false, example = "...")
    val accessToken: String
}