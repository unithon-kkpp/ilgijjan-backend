package com.ilgijjan.common.config

import com.ilgijjan.common.log.MdcTaskDecorator
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
            corePoolSize = 24     // 컨슈머 동시성 최대 12 × (음악+이미지 2개) = 24개 즉시 병렬 처리
            maxPoolSize = 32
            queueCapacity = 100
            keepAliveSeconds = 30
            setThreadNamePrefix("AsyncThread-")
            setTaskDecorator(MdcTaskDecorator())
            initialize()
        }
    }
}
