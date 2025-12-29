package com.ilgijjan.integration.notification.infrastructure

import com.ilgijjan.integration.notification.application.NotificationSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmNotificationSender(
    private val fcmClient: FcmClient
): NotificationSender {
    @Transactional
    override fun sendDiaryCompletion(tokens: List<String>, diaryId: Long): List<String> {
        if (tokens.isEmpty()) {
            return emptyList()
        }

        return fcmClient.sendMulticast(
            tokens = tokens,
            title = "일기짠! 노래 완성 ✨",
            body = "일기로 만든 노래를 들어보세요!",
            data = mapOf("diaryId" to diaryId.toString())
        )
    }
}
