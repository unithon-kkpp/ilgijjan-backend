package com.ilgijjan.domain.billing.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.billing.infrastructure.PaymentHistoryRepository
import org.springframework.stereotype.Component

@Component
class BillingValidator(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    fun validateNotProcessed(purchaseToken: String) {
        if (paymentHistoryRepository.existsById(purchaseToken)) {
            throw CustomException(ErrorCode.DUPLICATE_PURCHASE_TOKEN)
        }
    }
}
