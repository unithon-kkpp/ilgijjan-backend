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

    @Bean(name = ["diaryTaskExecutor"])
    fun diaryTaskExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 30    // 동시 처리할 일기 수 상한 (Gemini Rate Limit 기준)
            maxPoolSize = 30
            queueCapacity = 200
            keepAliveSeconds = 30
            setThreadNamePrefix("DiaryTask-")
            setTaskDecorator(MdcTaskDecorator())
            initialize()
        }
    }

    @Bean(name = ["asyncExecutor"])
    fun asyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 60    // 동시 처리 일기(30) × 자식 작업 수(music 1 + image 1 = 2) = 60
            maxPoolSize = 60
            queueCapacity = 200
            keepAliveSeconds = 30
            setThreadNamePrefix("AsyncThread-")
            setTaskDecorator(MdcTaskDecorator())
            initialize()
        }
    }
}
