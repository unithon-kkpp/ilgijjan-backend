package com.ilgijjan.domain.fcmtoken.application

import com.ilgijjan.domain.fcmtoken.infrastructure.FcmTokenRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FcmTokenDeleter(
    private val fcmTokenRepository: FcmTokenRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun deleteByTokens(tokens: List<String>) {
        if (tokens.isEmpty()) return
        fcmTokenRepository.deleteByTokenIn(tokens)
        log.info("FCM 토큰 삭제 완료 - 삭제된 개수: ${tokens.size}")
    }
}
