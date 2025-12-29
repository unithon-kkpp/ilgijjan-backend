package com.ilgijjan.domain.fcmtoken.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FcmTokenService (
    private val fcmTokenCreator: FcmTokenCreator,
    private val fcmTokenUpdater: FcmTokenUpdater
) {
    @Transactional
    fun registerToken(userId: Long, token: String) {
        fcmTokenCreator.create(userId, token)
    }

    @Transactional
    fun renewToken(token: String) {
        fcmTokenUpdater.updateLastUsedAt(token)
    }
}
