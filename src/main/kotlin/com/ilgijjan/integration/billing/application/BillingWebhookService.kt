package com.ilgijjan.integration.billing.application

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.integration.billing.infrastructure.BillingWebhookHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BillingWebhookService(
    private val handlers: List<BillingWebhookHandler>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun processWebhook(storePath: String, rawBody: String) {
        val type = StoreType.fromPath(storePath) ?: run {
            log.warn("지원하지 않는 스토어 타입 요청입니다: {}", storePath)
            throw IllegalArgumentException("Invalid store type: $storePath")
        }

        val handler = handlers.find { it.getStoreType() == type } ?: run {
            log.error("해당 스토어 타입을 처리할 핸들러가 없습니다: {}", type)
            throw IllegalStateException("No handler found for: $type")
        }

        log.info("Billing Webhook 처리 시작 - Store: {}, Body: {}", type, rawBody)
        handler.handle(rawBody)
    }
}
