package com.ilgijjan.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync(proxyTargetClass = true)
@Configuration
class AsyncConfig {

    @Bean(name = ["asyncExecutor"])
    fun asyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 4
            maxPoolSize = 8
            queueCapacity = 100
            keepAliveSeconds = 30
            setThreadNamePrefix("AsyncThread-")
            initialize()
        }
    }
}