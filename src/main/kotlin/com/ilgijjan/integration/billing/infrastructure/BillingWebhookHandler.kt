package com.ilgijjan.integration.billing.infrastructure

import com.ilgijjan.domain.billing.domain.StoreType

interface BillingWebhookHandler {
    fun getStoreType(): StoreType
    fun handle(rawBody: String)
}
