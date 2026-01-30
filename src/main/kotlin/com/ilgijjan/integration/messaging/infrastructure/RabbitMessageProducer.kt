package com.ilgijjan.integration.messaging.infrastructure

import com.ilgijjan.integration.messaging.application.MessageProducer
import org.slf4j.MDC
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitMessageProducer(
    private val rabbitTemplate: RabbitTemplate
) : MessageProducer {
    override fun sendMessage(exchange: String, routingKey: String, payload: Any) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload) { message ->
            message.messageProperties.headers["requestId"] = MDC.get("requestId")
            message
        }
    }
}
