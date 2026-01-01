package com.ilgijjan.domain.auth.application

import com.ilgijjan.common.jwt.TokenType
import com.ilgijjan.integration.cache.application.CacheService
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TokenManager(
    private val cacheService: CacheService
) {
    companion object {
        private const val RT_PREFIX = "RT:"
    }

    fun saveRefreshToken(userId: Long, token: String) {
        cacheService.set(
            key = "$RT_PREFIX$userId",
            value = token,
            duration = Duration.ofMillis(TokenType.REFRESH.lifeTime)
        )
    }

    fun getRefreshToken(userId: Long): String? {
        return cacheService.get("$RT_PREFIX$userId")
    }

    fun deleteRefreshToken(userId: Long) {
        cacheService.delete("$RT_PREFIX$userId")
    }
}
