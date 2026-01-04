package com.ilgijjan.domain.billing.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentHistoryUpdater(
    private val paymentHistoryReader: PaymentHistoryReader
) {

    @Transactional
    fun complete(purchaseToken: String) {
        val history = paymentHistoryReader.getByPurchaseToken(purchaseToken)
        history.complete()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun consumeFail(purchaseToken: String) {
        val history = paymentHistoryReader.getByPurchaseToken(purchaseToken)
        history.consumeFail()
    }
}
