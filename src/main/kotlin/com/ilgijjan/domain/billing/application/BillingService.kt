package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.presentation.BillingVerifyRequest
import org.springframework.stereotype.Service

@Service
class BillingService(
    private val oneStoreManager: OneStoreManager,
    private val billingValidator: BillingValidator,
    private val billingTransactionHandler: BillingTransactionHandler,
    private val paymentHistoryUpdater: PaymentHistoryUpdater
) {
    fun verifyPurchase(userId: Long, request: BillingVerifyRequest) {
        when (request.storeType) {
            StoreType.ONE_STORE -> {
                processOneStore(userId, request.storeProductId, request.purchaseToken!!)
            }
            StoreType.GOOGLE -> {}
            StoreType.APPLE -> {}
        }
    }

    private fun processOneStore(userId: Long, storeProductId: String, purchaseToken: String) {
        billingValidator.validateNotProcessed(purchaseToken)

        oneStoreManager.verifyPurchase(storeProductId, purchaseToken)

        billingTransactionHandler.chargeAndPrepare(userId, storeProductId, purchaseToken)

        oneStoreManager.consumePurchase(storeProductId, purchaseToken)

        paymentHistoryUpdater.complete(purchaseToken)
    }
}
