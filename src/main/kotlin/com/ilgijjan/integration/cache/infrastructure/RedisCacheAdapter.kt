package com.ilgijjan.integration.cache.infrastructure

import com.ilgijjan.integration.cache.application.CacheService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisCacheAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : CacheService {
    override fun set(key: String, value: String, duration: Duration) {
        redisTemplate.opsForValue().set(key, value, duration)
    }

    override fun get(key: String): String? = redisTemplate.opsForValue().get(key)

    override fun hasKey(key: String): Boolean = redisTemplate.hasKey(key)

    override fun delete(key: String) {
        redisTemplate.delete(key)
    }
}
