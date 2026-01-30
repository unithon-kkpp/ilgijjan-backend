package com.ilgijjan.integration.messaging.infrastructure

import com.ilgijjan.common.config.RabbitMqConfig
import com.ilgijjan.common.utils.IdGenerator
import com.ilgijjan.domain.diary.application.CreateDiaryEvent
import org.slf4j.MDC
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.ApplicationEventPublisher
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class RabbitDiaryMessageListener(
    private val eventPublisher: ApplicationEventPublisher
) {
    @RabbitListener(queues = [RabbitMqConfig.DIARY_QUEUE])
    fun onMessage(diaryId: Long, @Header(name = "requestId", required = false) requestId: String?) {
        MDC.put("requestId", requestId ?: IdGenerator.generateTraceId())

        try {
            eventPublisher.publishEvent(CreateDiaryEvent(diaryId))
        } finally {
            MDC.clear()
        }
    }
}
