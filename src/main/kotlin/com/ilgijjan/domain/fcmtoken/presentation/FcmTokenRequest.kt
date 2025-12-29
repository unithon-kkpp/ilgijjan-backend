package com.ilgijjan.domain.fcmtoken.presentation

import io.swagger.v3.oas.annotations.media.Schema

data class FcmTokenRequest (
    @field:Schema(description = "FCM Token", nullable = false, example = "...")
    val token: String
)
