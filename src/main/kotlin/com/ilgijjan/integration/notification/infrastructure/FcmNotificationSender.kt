package com.ilgijjan.integration.notification.infrastructure

import com.ilgijjan.integration.notification.application.NotificationSender
import org.springframework.stereotype.Component

@Component
class FcmNotificationSender(
    private val fcmClient: FcmClient
): NotificationSender {
    override fun sendDiaryCompletion(tokens: List<String>, diaryId: Long): List<String> {
        return send(
            tokens = tokens,
            title = "일기짠! 노래 완성 ✨",
            body = "일기로 만든 노래를 들어보세요!",
            data = mapOf(
                "diaryId" to diaryId.toString(),
                "status" to "COMPLETED"
            )
        )
    }

    override fun sendDiaryFailure(tokens: List<String>, diaryId: Long): List<String> {
        return send(
            tokens = tokens,
            title = "일기짠! 노래 생성 실패 😢",
            body = "노래를 만들지 못했어요. 사용한 음표는 돌려드렸어요!",
            data = mapOf(
                "diaryId" to diaryId.toString(),
                "status" to "FAILED"
            )
        )
    }

    override fun sendTest(tokens: List<String>): List<String> {
        return send(
            tokens = tokens,
            title = "테스트 알림입니다 🔔",
            body = "알림 전송에 성공했습니다!",
            data = mapOf("type" to "TEST")
        )
    }

    private fun send(tokens: List<String>, title: String, body: String, data: Map<String, String>): List<String> {
        if (tokens.isEmpty()) return emptyList()
        return fcmClient.sendMulticast(tokens, title, body, data)
    }
}
