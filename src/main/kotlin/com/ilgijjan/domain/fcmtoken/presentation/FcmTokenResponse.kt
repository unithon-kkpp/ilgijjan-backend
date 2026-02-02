package com.ilgijjan.domain.fcmtoken.presentation

import com.ilgijjan.domain.fcmtoken.domain.FcmToken
import java.time.LocalDateTime

data class FcmTokenResponse(
    val id: Long,
    val token: String,
    val lastUsedAt: LocalDateTime,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(fcmToken: FcmToken) = FcmTokenResponse(
            id = fcmToken.id!!,
            token = fcmToken.token,
            lastUsedAt = fcmToken.lastUsedAt,
            createdAt = fcmToken.createdAt!!
        )
    }
}
