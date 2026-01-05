package com.ilgijjan.integration.billing.infrastructure

data class OneStorePnsRequest(
    val msgVersion: String,
    val clientId: String,
    val productId: String,
    val messageType: String,
    val purchaseId: String,
    val developerPayload: String?,
    val purchaseTimeMillis: Long,
    val purchaseState: String,
    val purchaseToken: String,
    val signature: String
)
