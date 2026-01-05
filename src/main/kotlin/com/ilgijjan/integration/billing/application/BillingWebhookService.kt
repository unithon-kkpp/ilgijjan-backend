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
            log.warn("[Webhook] 지원하지 않는 스토어 경로 요청 수신 (정상적인 호출인지 확인 필요): path={}", storePath)
            return
        }

        val handler = handlers.find { it.getStoreType() == type } ?: run {
            log.error("[Webhook Critical] {} 타입 핸들러가 등록되지 않았습니다. 즉시 핸들러 등록 코드를 확인하십시오.", type)
            throw IllegalStateException("No handler found for: $type")
        }

        log.info("Billing Webhook 처리 시작 - Store: {}, Body: {}", type, rawBody)
        handler.handle(rawBody)
    }
}
