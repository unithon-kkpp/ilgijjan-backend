package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.presentation.BillingVerifyRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BillingService(
    private val oneStoreManager: OneStoreManager,
    private val billingValidator: BillingValidator,
    private val storeProductReader: StoreProductReader,
    private val userWalletManager: UserWalletManager,
    private val paymentHistoryUpdater: PaymentHistoryUpdater
) {
    @Transactional
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

        val storeProduct = storeProductReader.getByStoreProductId(storeProductId)
        userWalletManager.charge(userId, storeProduct.product.noteAmount)
        paymentHistoryUpdater.saveSuccessHistory(userId, storeProduct, purchaseToken)

        oneStoreManager.consumePurchase(storeProductId, purchaseToken)
    }
}
