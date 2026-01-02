package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import com.ilgijjan.domain.auth.domain.BlacklistReason
import com.ilgijjan.integration.cache.application.CacheService
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TokenManager(
    private val cacheService: CacheService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    companion object {
        private const val REFRESH_TOKEN_PREFIX = "RT:"
        private const val BLACK_LIST_PREFIX = "BL:"
    }

    fun saveRefreshToken(userId: Long, refreshToken: String) {
        cacheService.set(
            key = "$REFRESH_TOKEN_PREFIX$refreshToken",
            value = userId.toString(),
            duration = Duration.ofMillis(TokenType.REFRESH.lifeTime)
        )
    }

    fun validateAndDeleteRefreshToken(userId: Long, refreshToken: String) {
        val userIdStr = getAndDeleteRefreshToken(refreshToken)
        if (userIdStr == null || userIdStr != userId.toString()) {
            throw CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        }
    }

    fun consumeRefreshToken(refreshToken: String): Long {
        val userIdStr = getAndDeleteRefreshToken(refreshToken)
            ?: throw CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        return userIdStr.toLong()
    }

    private fun getAndDeleteRefreshToken(refreshToken: String): String? {
        return cacheService.getAndDelete("$REFRESH_TOKEN_PREFIX$refreshToken")
    }

    fun registerBlacklist(accessToken: String, reason: BlacklistReason) {
        val remainingTime = jwtTokenProvider.getRemainingTime(accessToken)
        if (remainingTime.isNegative || remainingTime.isZero) return

        cacheService.set(
            key = "$BLACK_LIST_PREFIX$accessToken",
            value = reason.name,
            duration = remainingTime
        )
    }

    fun validateNotBlacklisted(accessToken: String) {
        if (cacheService.hasKey("$BLACK_LIST_PREFIX$accessToken")) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }
    }
}
