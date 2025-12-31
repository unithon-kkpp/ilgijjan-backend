package com.ilgijjan.domain.auth.presentation

import io.swagger.v3.oas.annotations.media.Schema

data class LoginResponse(
    @field:Schema(description = "액세스 토큰", example = "...")
    val accessToken: String,
    @field:Schema(description = "리프레시 토큰", example = "...")
    val refreshToken: String,
    @field:Schema(description = "온보딩 유무", example = "true")
    val isOnboarded: Boolean
)
