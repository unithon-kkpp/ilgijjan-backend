package com.ilgijjan.integration.notification.infrastructure

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FcmClient(
    private val firebaseMessaging: FirebaseMessaging
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendMulticast(tokens: List<String>, title: String, body: String, data: Map<String, String>): List<String> {
        val message = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .putAllData(data)
            .build()

        val response = firebaseMessaging.sendEachForMulticast(message)
        log.info("FCM 전송 결과 - 성공: ${response.successCount}, 실패: ${response.failureCount}")

        return response.responses.mapIndexedNotNull { index, res ->
            if (!res.isSuccessful) {
                log.error("FCM 전송 실패 상세 - 토큰: ${tokens[index]}, 에러코드: ${res.exception.messagingErrorCode}, 메시지: ${res.exception.message}")
                if (isDeadToken(res.exception)) {
                    return@mapIndexedNotNull tokens[index]
                }
            }
            null
        }
    }

    private fun isDeadToken(e: FirebaseMessagingException) =
        e.messagingErrorCode in listOf(
            MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT
        )
}
