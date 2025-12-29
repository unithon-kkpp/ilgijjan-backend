package com.ilgijjan.domain.fcmtoken.application

import org.springframework.stereotype.Component

@Component
class FcmTokenUpdater(
    private val fcmTokenReader: FcmTokenReader
) {
    fun updateLastUsedAt(token: String) {
        val fcmToken = fcmTokenReader.getByToken(token)
        fcmToken.updateLastUsed()
    }
}
