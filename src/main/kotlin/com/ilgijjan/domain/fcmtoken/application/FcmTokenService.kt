package com.ilgijjan.domain.fcmtoken.application

import com.ilgijjan.domain.fcmtoken.presentation.FcmTokenResponse
import com.ilgijjan.integration.notification.application.NotificationSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FcmTokenService (
    private val fcmTokenCreator: FcmTokenCreator,
    private val fcmTokenUpdater: FcmTokenUpdater,
    private val fcmTokenReader: FcmTokenReader,
    private val fcmTokenDeleter: FcmTokenDeleter,
    private val notificationSender: NotificationSender
) {
    @Transactional
    fun registerToken(userId: Long, token: String) {
        fcmTokenCreator.create(userId, token)
    }

    @Transactional
    fun renewToken(token: String) {
        fcmTokenUpdater.updateLastUsedAt(token)
    }

    fun getTokens(userId: Long): List<FcmTokenResponse> {
        return fcmTokenReader.findAllByUserId(userId).map { FcmTokenResponse.from(it) }
    }

    @Transactional
    fun sendTestNotification(userId: Long) {
        val tokens = fcmTokenReader.findAllByUserId(userId).map { it.token }
        val deadTokens = notificationSender.sendTest(tokens)
        fcmTokenDeleter.deleteByTokens(deadTokens)
    }
}
