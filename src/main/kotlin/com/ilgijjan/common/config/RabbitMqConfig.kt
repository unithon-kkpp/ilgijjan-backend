package com.ilgijjan.common.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {
    companion object {
        const val DIARY_QUEUE = "diary.create.queue"
        const val DIARY_EXCHANGE = "diary.exchange"
        const val DIARY_ROUTING_KEY = "diary.create.key"
    }

    @Bean
    fun diaryQueue(): Queue {
        return Queue(DIARY_QUEUE, true)
    }

    @Bean
    fun diaryExchange(): TopicExchange {
        return TopicExchange(DIARY_EXCHANGE)
    }

    @Bean
    fun diaryBinding(diaryQueue: Queue, diaryExchange: TopicExchange): Binding {
        return BindingBuilder.bind(diaryQueue).to(diaryExchange).with(DIARY_ROUTING_KEY)
    }

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }
}
