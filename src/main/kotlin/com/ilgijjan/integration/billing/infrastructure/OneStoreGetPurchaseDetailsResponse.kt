package com.ilgijjan.integration.billing.infrastructure

data class OneStoreGetPurchaseDetailsResponse(
    val consumptionState: Int,
    val purchaseState: Int,
    val purchaseTime: Long,
    val purchaseId: String,
    val acknowledgeState: Int,
    val quantity: Int
)
