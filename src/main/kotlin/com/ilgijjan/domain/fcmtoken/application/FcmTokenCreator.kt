package com.ilgijjan.domain.fcmtoken.application

import com.ilgijjan.domain.fcmtoken.domain.FcmToken
import com.ilgijjan.domain.fcmtoken.infrastructure.FcmTokenRepository
import org.springframework.stereotype.Component

@Component
class FcmTokenCreator(
    private val fcmTokenReader: FcmTokenReader,
    private val fcmTokenRepository: FcmTokenRepository
) {
    fun create(userId: Long, token: String) {
        if (!fcmTokenReader.existsByToken(token)) {
            fcmTokenRepository.save(FcmToken(userId = userId, token = token))
        }
    }
}
