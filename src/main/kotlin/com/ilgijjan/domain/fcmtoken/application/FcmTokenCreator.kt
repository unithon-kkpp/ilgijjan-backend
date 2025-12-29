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

        val existingToken = fcmTokenReader.findByToken(token)

        if (existingToken == null) {
            fcmTokenRepository.save(FcmToken(userId = userId, token = token))
        } else {
            if (existingToken.userId != userId) {
                existingToken.updateUserId(userId)
            }
            existingToken.updateLastUsed()
        }
    }
}
