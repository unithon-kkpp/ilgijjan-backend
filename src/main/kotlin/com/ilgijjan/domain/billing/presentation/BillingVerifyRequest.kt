package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreType

@ValidBillingRequest
data class BillingVerifyRequest(
    val storeType: StoreType,
    val storeProductId: String,
    val purchaseToken: String?,
)
