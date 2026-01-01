package com.ilgijjan.integration.cache.application

import java.time.Duration

interface CacheService {
    fun set(key: String, value: String, duration: Duration)
    fun get(key: String): String?
    fun hasKey(key: String): Boolean
    fun delete(key: String)
}
