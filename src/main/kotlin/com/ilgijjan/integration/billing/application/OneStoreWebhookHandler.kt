package com.ilgijjan.integration.billing.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.billing.application.BillingTransactionHandler
import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.integration.billing.infrastructure.OneStorePnsRequest
import com.ilgijjan.integration.billing.infrastructure.BillingWebhookHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OneStoreWebhookHandler(
    private val oneStoreSignatureVerifier: OneStoreSignatureVerifier,
    private val billingTransactionHandler: BillingTransactionHandler,
    private val objectMapper: ObjectMapper
) : BillingWebhookHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getStoreType(): StoreType = StoreType.ONE_STORE

    override fun handle(rawBody: String) {
        val request = objectMapper.readValue(rawBody, OneStorePnsRequest::class.java)
        oneStoreSignatureVerifier.validate(rawBody, request.signature)

        if (request.purchaseState == "CANCELED") {
            try {
                billingTransactionHandler.refund(request.purchaseToken)
            } catch (e: CustomException) {
                if (e.errorCode == ErrorCode.PAYMENT_HISTORY_NOT_FOUND) {
                    log.warn("[Webhook Info] 원스토어 환불 처리를 중단합니다. DB에 결제 이력이 존재하지 않아 재시도해도 처리가 불가능합니다. token: {}", request.purchaseToken)
                    return
                }
                log.error("[Webhook Retry] 환불 로직 실행 중 비즈니스 예외 발생. 재시도를 위해 에러를 전파합니다. message: {}", e.message)
                throw e
            }
        }
    }
}
