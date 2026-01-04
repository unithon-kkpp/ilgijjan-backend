package com.ilgijjan.domain.billing.application

import com.ilgijjan.common.annotation.TraceConsumeFailedPurchase
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.integration.billing.infrastructure.OneStoreBillingClient
import org.springframework.stereotype.Component

@Component
class OneStoreManager(
    private val oneStoreClient: OneStoreBillingClient
) {
    companion object {
        private const val PURCHASE_STATE_COMPLETED = 0
        private const val CONSUMPTION_STATE_NOT_CONSUMED = 0
    }

    fun verifyPurchase(storeProductId: String, purchaseToken: String) {
        val detail = oneStoreClient.getPurchaseDetails(storeProductId, purchaseToken)

        if (detail.purchaseState != PURCHASE_STATE_COMPLETED) {
            throw CustomException(ErrorCode.INVALID_PURCHASE_STATE)
        }
        if (detail.consumptionState != CONSUMPTION_STATE_NOT_CONSUMED) {
            throw CustomException(ErrorCode.ALREADY_CONSUMED_PURCHASE)
        }
    }

    @TraceConsumeFailedPurchase
    fun consumePurchase(storeProductId: String, purchaseToken: String) {
        oneStoreClient.consumePurchase(storeProductId, purchaseToken)
    }
}
