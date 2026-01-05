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

    companion object {
        private const val PURCHASE_STATE_CANCELED = "CANCELED"
    }

    override fun getStoreType(): StoreType = StoreType.ONE_STORE

    override fun handle(rawBody: String) {
        val request = objectMapper.readValue(rawBody, OneStorePnsRequest::class.java)
        oneStoreSignatureVerifier.validate(rawBody, request.signature)

        if (request.purchaseState == PURCHASE_STATE_CANCELED) {
            try {
                billingTransactionHandler.refund(request.purchaseToken)
            } catch (e: CustomException) {
                when (e.errorCode) {
                    ErrorCode.PAYMENT_HISTORY_NOT_FOUND -> {
                        log.warn("[Refund Skip] 결제 이력이 존재하지 않아 환불을 중단합니다. token: {}", request.purchaseToken)
                        return
                    }
                    ErrorCode.INSUFFICIENT_NOTES -> {
                        log.warn("[Refund Denied] 보유 음표 부족으로 환불 처리가 불가능합니다. token: {}", request.purchaseToken)
                        return
                    }
                    else -> {
                        log.error("[Webhook Retry] 환불 로직 실행 중 비즈니스 예외 발생. 재시도를 위해 에러를 전파합니다. message: {}", e.message)
                        throw e
                    }
                }
            }
        }
    }
}
