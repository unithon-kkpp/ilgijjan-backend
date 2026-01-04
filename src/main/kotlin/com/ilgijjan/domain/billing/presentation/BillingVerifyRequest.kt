package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreType
import io.swagger.v3.oas.annotations.media.Schema

@ValidBillingRequest
data class BillingVerifyRequest(
    @field:Schema(description = "스토어 타입 (ONE_STORE, GOOGLE, APPLE)", example = "ONE_STORE")
    val storeType: StoreType,
    @field:Schema(description = "스토어별 상품 ID", example = "notes_100")
    val storeProductId: String,
    @field:Schema(description = " 구매완료 시 전달받은 원스토어 구매 토큰", example = "GPA.1234-...")
    val purchaseToken: String?,
)
