package com.ilgijjan.integration.messaging.infrastructure

import com.ilgijjan.common.config.RabbitMqConfig
import com.ilgijjan.domain.diary.application.CreateDiaryEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class RabbitDiaryMessageListener(
    private val eventPublisher: ApplicationEventPublisher
) {
    @RabbitListener(queues = [RabbitMqConfig.DIARY_QUEUE])
    fun onMessage(diaryId: Long) {
        eventPublisher.publishEvent(CreateDiaryEvent(diaryId))    }
}
