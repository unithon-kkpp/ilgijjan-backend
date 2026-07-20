package com.ilgijjan.common.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ilgijjan.common.constants.CacheConstants
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .withCacheConfiguration(CacheConstants.PUBLIC_DIARIES_CACHE, publicDiariesCacheConfig())
            .disableCreateOnMissingCache()
            .enableStatistics()
            .build()
    }

    private fun publicDiariesCacheConfig(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(valueSerializer()))
    }

    private fun valueSerializer(): GenericJackson2JsonRedisSerializer {
        return GenericJackson2JsonRedisSerializer.builder()
            .objectMapper(jacksonObjectMapper())
            .defaultTyping(true)
            .build()
    }
}
