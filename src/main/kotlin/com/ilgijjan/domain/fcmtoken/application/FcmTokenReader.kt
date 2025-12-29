package com.ilgijjan.domain.fcmtoken.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.fcmtoken.domain.FcmToken
import com.ilgijjan.domain.fcmtoken.infrastructure.FcmTokenRepository
import org.springframework.stereotype.Component

@Component
class FcmTokenReader(
    private val fcmTokenRepository: FcmTokenRepository
) {
    fun findAllByUserId(userId: Long): List<FcmToken> {
        return fcmTokenRepository.findAllByUserId(userId)
    }

    fun getByToken(token: String): FcmToken {
        return fcmTokenRepository.findByToken(token)
            ?: throw CustomException(ErrorCode.FCM_TOKEN_NOT_FOUND)
    }

    fun findByToken(token: String): FcmToken? {
        return fcmTokenRepository.findByToken(token)
    }


    fun existsByToken(token: String): Boolean {
        return fcmTokenRepository.existsByToken(token)
    }
}
