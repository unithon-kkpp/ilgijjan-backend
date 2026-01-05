package com.ilgijjan.integration.billing.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilgijjan.domain.billing.application.BillingTransactionHandler
import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.integration.billing.infrastructure.OneStorePnsRequest
import com.ilgijjan.integration.billing.infrastructure.BillingWebhookHandler
import org.springframework.stereotype.Component

@Component
class OneStoreWebhookHandler(
    private val oneStoreSignatureVerifier: OneStoreSignatureVerifier,
    private val billingTransactionHandler: BillingTransactionHandler,
    private val objectMapper: ObjectMapper
) : BillingWebhookHandler {

    override fun getStoreType(): StoreType = StoreType.ONE_STORE

    override fun handle(rawBody: String) {
        val request = objectMapper.readValue(rawBody, OneStorePnsRequest::class.java)

        require(oneStoreSignatureVerifier.verify(rawBody, request.signature)) { "Invalid OneStore Signature" }

        if (request.purchaseState == "CANCELED") {
            billingTransactionHandler.refund(request.purchaseToken)
        }
    }
}
