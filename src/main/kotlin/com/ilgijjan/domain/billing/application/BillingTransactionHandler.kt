package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.PaymentStatus
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BillingTransactionHandler(
    private val storeProductReader: StoreProductReader,
    private val userWalletUpdater: UserWalletUpdater,
    private val paymentHistoryCreator: PaymentHistoryCreator,
    private val paymentHistoryReader: PaymentHistoryReader
) {
    @Transactional
    fun chargeAndPrepare(userId: Long, storeProductId: String, purchaseToken: String) {
        val storeProduct = storeProductReader.getByStoreProductId(storeProductId)
        userWalletUpdater.charge(userId, storeProduct.product.noteAmount)
        paymentHistoryCreator.create(userId, storeProduct, purchaseToken)
    }

    @Transactional
    fun refund(purchaseToken: String) {
        val history = paymentHistoryReader.getByPurchaseTokenWithProduct(purchaseToken)
        if (history.status == PaymentStatus.REFUNDED) return

        val amountToRevoke = history.storeProduct.product.noteAmount
        userWalletUpdater.revoke(history.userId, amountToRevoke)

        history.refund()
    }
}
