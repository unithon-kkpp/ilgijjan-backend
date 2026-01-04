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
    fun verifyPurchase(storeProductId: String, purchaseToken: String) {
        val detail = oneStoreClient.getPurchaseDetails(storeProductId, purchaseToken)

        if (detail.purchaseState != 0) {
            throw CustomException(ErrorCode.INVALID_PURCHASE_STATE)
        }
        if (detail.consumptionState != 0) {
            throw CustomException(ErrorCode.ALREADY_CONSUMED_PURCHASE)
        }
    }

    @TraceConsumeFailedPurchase
    fun consumePurchase(storeProductId: String, purchaseToken: String) {
        oneStoreClient.consumePurchase(storeProductId, purchaseToken)
    }
}
