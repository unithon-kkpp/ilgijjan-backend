package com.ilgijjan.common.config

import com.ilgijjan.common.log.MdcTaskDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@EnableAsync(proxyTargetClass = true)
@Configuration
class AsyncConfig {

    @Bean(name = ["diaryTaskExecutor"])
    fun diaryTaskExecutor(): Executor =
        Executors.newVirtualThreadPerTaskExecutor()

    @Bean(name = ["asyncExecutor"])
    fun asyncExecutor(): Executor =
        Executors.newVirtualThreadPerTaskExecutor()
}
