package com.ilgijjan.integration.messaging.application

interface MessageProducer {
    fun sendMessage(exchange: String, routingKey: String, payload: Any)
}
