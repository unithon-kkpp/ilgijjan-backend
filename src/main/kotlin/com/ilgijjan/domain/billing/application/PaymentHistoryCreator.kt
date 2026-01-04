package com.ilgijjan.domain.billing.application

import com.ilgijjan.domain.billing.domain.PaymentHistory
import com.ilgijjan.domain.billing.domain.PaymentStatus
import com.ilgijjan.domain.billing.domain.StoreProduct
import com.ilgijjan.domain.billing.infrastructure.PaymentHistoryRepository
import org.springframework.stereotype.Component

@Component
class PaymentHistoryCreator(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    fun create(userId: Long, storeProduct: StoreProduct, purchaseToken: String): PaymentHistory {
        return paymentHistoryRepository.save(
            PaymentHistory(
                purchaseToken = purchaseToken,
                userId = userId,
                storeProduct = storeProduct,
                status = PaymentStatus.PENDING
            )
        )
    }
}
