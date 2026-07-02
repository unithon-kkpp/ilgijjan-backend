package com.ilgijjan.common.config

import com.ilgijjan.common.log.MdcTaskDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor

@EnableAsync(proxyTargetClass = true)
@Configuration
class AsyncConfig {

    @Bean(name = ["diaryTaskExecutor"])
    fun diaryTaskExecutor(): Executor =
        SimpleAsyncTaskExecutor().apply {
            setVirtualThreads(true)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("DiaryTask-")
        }

    @Bean(name = ["asyncExecutor"])
    fun asyncExecutor(): Executor =
        SimpleAsyncTaskExecutor().apply {
            setVirtualThreads(true)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("AsyncThread-")
        }
}
