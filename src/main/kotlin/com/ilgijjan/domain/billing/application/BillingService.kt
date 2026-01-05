package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.presentation.BillingVerifyRequest
import com.ilgijjan.domain.billing.presentation.ReadProductsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BillingService(
    private val oneStoreManager: OneStoreManager,
    private val billingValidator: BillingValidator,
    private val billingTransactionHandler: BillingTransactionHandler,
    private val paymentHistoryUpdater: PaymentHistoryUpdater,
    private val storeProductReader: StoreProductReader
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

    @Transactional(readOnly = true)
    fun getProducts(storeType: StoreType): ReadProductsResponse {
        val storeProducts = storeProductReader.findAllByStoreType(storeType)
        return ReadProductsResponse.from(storeProducts)
    }
}
