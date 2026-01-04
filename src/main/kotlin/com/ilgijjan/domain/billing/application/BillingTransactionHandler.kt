package com.ilgijjan.domain.billing.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BillingTransactionHandler(
    private val storeProductReader: StoreProductReader,
    private val userWalletManager: UserWalletManager,
    private val paymentHistoryCreator: PaymentHistoryCreator
) {
    @Transactional
    fun chargeAndPrepare(userId: Long, storeProductId: String, purchaseToken: String) {
        val storeProduct = storeProductReader.getByStoreProductId(storeProductId)
        userWalletManager.charge(userId, storeProduct.product.noteAmount)
        paymentHistoryCreator.create(userId, storeProduct, purchaseToken)
    }
}
