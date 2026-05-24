package com.ilgijjan.common.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ilgijjan.domain.diary.presentation.ReadPublicDiariesResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {
    companion object {
        // 공개 일기 피드 캐시 (issue #56) - 모든 사용자에게 동일한 공유 응답이라 캐싱 안전
        const val PUBLIC_DIARIES_CACHE = "publicDiaries"
    }

    // app.cache.enabled=false 로 캐싱 비활성화 가능 (성능 측정 시 before/after 비교용)
    @Bean
    @ConditionalOnProperty(name = ["app.cache.enabled"], havingValue = "true", matchIfMissing = true)
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        // 공개 피드 응답은 단일 타입(ReadPublicDiariesResponse)이라 타입 지정 직렬화로 안정적으로 처리
        val objectMapper = jacksonObjectMapper()
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, ReadPublicDiariesResponse::class.java)

        val publicDiariesConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))   // 무효화 누락 대비 안전망 TTL
            .disableCachingNullValues()
            .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(valueSerializer))

        return RedisCacheManager.builder(connectionFactory)
            .withCacheConfiguration(PUBLIC_DIARIES_CACHE, publicDiariesConfig)
            .build()
    }

    @Bean
    @ConditionalOnProperty(name = ["app.cache.enabled"], havingValue = "false")
    fun noOpCacheManager(): CacheManager = NoOpCacheManager()
}
