package com.ilgijjan.domain.fcmtoken.application

import com.ilgijjan.domain.fcmtoken.infrastructure.FcmTokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class FcmTokenDeleter(
    private val fcmTokenRepository: FcmTokenRepository
) {
    @Transactional
    fun deleteByTokens(tokens: List<String>) {
        if (tokens.isNotEmpty()) {
            fcmTokenRepository.deleteByTokenIn(tokens)
        }
    }
}
